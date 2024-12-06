-- MySQL Script generated by MySQL Workbench
-- Thu May 11 07:57:05 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema reportcard
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `reportcard` ;

-- -----------------------------------------------------
-- Schema reportcard
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reportcard` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `reportcard` ;

-- -----------------------------------------------------
-- Table `reportcard`.`company`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`company` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`company` (
  `company_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `company_name` VARCHAR(255) NOT NULL DEFAULT '''',
  PRIMARY KEY (`company_id`),
  UNIQUE INDEX `company_name_idx` (`company_name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`org`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`org` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`org` (
  `org_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `org_name` VARCHAR(255) NOT NULL DEFAULT '''',
  `company_fk` int unsigned NOT NULL,
  PRIMARY KEY (`org_id`),
  UNIQUE KEY `org_name_idx` (`org_name` ASC, `company_fk` ASC),
  KEY `FK_COMPANY_ORG_idx` (`company_fk`),
  CONSTRAINT `FK_COMPANY_ORG` FOREIGN KEY (`company_fk`) REFERENCES `company` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- -----------------------------------------------------
-- Table `reportcard`.`repo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`repo` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`repo` (
  `repo_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `repo_name` VARCHAR(255) NOT NULL,
  `org_fk` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`repo_id`),
  UNIQUE INDEX `repo_name_idx` (`org_fk` ASC, `repo_name` ASC) VISIBLE,
  INDEX `org_idx` (`org_fk` ASC) VISIBLE,
  CONSTRAINT `repo_org_fk`
    FOREIGN KEY (`org_fk`)
    REFERENCES `reportcard`.`org` (`org_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`branch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`branch` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`branch` (
  `branch_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `branch_name` VARCHAR(255) NOT NULL,
  `repo_fk` INT UNSIGNED NOT NULL,
  `last_run` datetime DEFAULT (utc_timestamp()),
  PRIMARY KEY (`branch_id`),
  INDEX `branch_repo_idx` (`repo_fk` ASC) VISIBLE,
  UNIQUE INDEX `branch_name_uq` (`branch_name` ASC, `repo_fk` ASC) VISIBLE,
  CONSTRAINT `branch_repo_fk`
    FOREIGN KEY (`repo_fk`)
    REFERENCES `reportcard`.`repo` (`repo_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`job` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`job` (
  `job_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `job_info` JSON NULL DEFAULT NULL,
  `branch_fk` INT UNSIGNED NOT NULL,
  `last_run` datetime DEFAULT (utc_timestamp()),
  `job_info_str` VARCHAR(512) GENERATED ALWAYS AS (job_info) VIRTUAL,
  PRIMARY KEY (`job_id`),
  UNIQUE KEY `UQ_BRANCH_FK_JOB_INFO_STR` (`job_info_str`,`branch_fk`),
  INDEX `FK_JOB_BRANCH` (`branch_fk` ASC) VISIBLE,
  CONSTRAINT `FK_JOB_BRANCH`
    FOREIGN KEY (`branch_fk`)
    REFERENCES `reportcard`.`branch` (`branch_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`run`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`run` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`run` (
  `run_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `run_reference` VARCHAR(255) NOT NULL,
  `job_fk` BIGINT UNSIGNED NOT NULL,
  `job_run_count` MEDIUMINT NULL DEFAULT NULL,
  `sha` VARCHAR(128) NULL DEFAULT NULL,
  `run_date` DATETIME NOT NULL DEFAULT (utc_timestamp()),
  `is_success` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`run_id`),
  UNIQUE INDEX `run_id_unique` (`run_id` ASC) VISIBLE,
  UNIQUE INDEX `uq_run_job_reference` (`job_fk` ASC, `run_reference` ASC) VISIBLE,
  INDEX `run_job_fk_idx` (`job_fk` ASC) VISIBLE,
  INDEX `run_job_sha` (`job_fk` ASC, `sha` ASC) VISIBLE,
  CONSTRAINT `run_job_fk`
    FOREIGN KEY (`job_fk`)
    REFERENCES `reportcard`.`job` (`job_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`stage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`stage` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`stage` (
  `stage_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `stage_name` VARCHAR(255) NOT NULL,
  `run_fk` BIGINT UNSIGNED NOT NULL,
  `test_result_json` JSON NULL,
  PRIMARY KEY (`stage_id`),
  UNIQUE INDEX `stage_id_unique` (`stage_id` ASC) VISIBLE,
  UNIQUE INDEX `uq_run_stage_name` (`stage_name` ASC, `run_fk` ASC) VISIBLE,
  INDEX `stage_run_fk_idx` (`run_fk` ASC) VISIBLE,
  CONSTRAINT `stage_run_fk`
    FOREIGN KEY (`run_fk`)
    REFERENCES `reportcard`.`run` (`run_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `reportcard`.`storage_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`storage_type` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`storage_type` (
  `storage_type_id` TINYINT NOT NULL AUTO_INCREMENT,
  `storage_type_name` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`storage_type_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `reportcard`.`storage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`storage` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`storage` (
  `storage_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `stage_fk` bigint unsigned NOT NULL,
  `label` varchar(64) NOT NULL,
  `prefix` varchar(1024) NOT NULL,
  `index_file` varchar(1024) DEFAULT NULL,
  `storage_type` tinyint DEFAULT NULL,
  `is_upload_complete` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`storage_id`),
  UNIQUE KEY `UQ_STABLE_LABEL` (`stage_fk`,`label`),
  KEY `stage_fk_idx` (`stage_fk`),
  KEY `storage_type_fk_idx` (`storage_type`),
  CONSTRAINT `stage_fk` FOREIGN KEY (`stage_fk`) REFERENCES `stage` (`stage_id`),
  CONSTRAINT `storage_type_fk` FOREIGN KEY (`storage_type`) REFERENCES `storage_type` (`storage_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- -----------------------------------------------------
-- Table `reportcard`.`test_status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`test_status` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`test_status` (
  `test_status_id` TINYINT NOT NULL AUTO_INCREMENT,
  `test_status_name` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`test_status_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`test_result`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`test_result` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`test_result` (
  `test_result_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `stage_fk` BIGINT UNSIGNED NOT NULL,
  `tests` INT UNSIGNED NOT NULL,
  `skipped` INT UNSIGNED NOT NULL,
  `error` INT UNSIGNED NOT NULL,
  `failure` INT UNSIGNED NOT NULL,
  `time` DECIMAL(9,3) UNSIGNED NOT NULL,
  `test_result_created` DATETIME NOT NULL DEFAULT (utc_timestamp()),
  `external_links` JSON NULL DEFAULT NULL,
  `is_success` TINYINT(1) GENERATED ALWAYS AS (`tests` > 0 && (`failure` + `error`) = 0) VIRTUAL,
  `has_skip` TINYINT(1) GENERATED ALWAYS AS (`tests` = 0 || `skipped` > 0) VIRTUAL,
  `test_suites_json` JSON DEFAULT NULL,
  PRIMARY KEY (`test_result_id`),
  INDEX `test_result_stage_fk_idx` (`stage_fk` ASC) VISIBLE,
  UNIQUE INDEX `stage_fk_UNIQUE` (`stage_fk` ASC) VISIBLE,
  CONSTRAINT `test_result_stage_fk`
    FOREIGN KEY (`stage_fk`)
    REFERENCES `reportcard`.`stage` (`stage_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `reportcard`.`test_suite`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`test_suite` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`test_suite` (
  `test_suite_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `test_result_fk` BIGINT UNSIGNED NOT NULL,
  `name` varchar(1024) NOT NULL,
  `tests` INT NOT NULL,
  `skipped` INT NOT NULL,
  `error` INT NOT NULL,
  `failure` INT NOT NULL,
  `time` DECIMAL(9,3) NOT NULL,
  `package_name` VARCHAR(1024) NULL DEFAULT NULL,
  `group` VARCHAR(1024) NULL DEFAULT NULL,
  `properties` JSON NULL DEFAULT NULL,
  `is_success` TINYINT(1) GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
  `has_skip` TINYINT(1) GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
  PRIMARY KEY (`test_suite_id`),
  INDEX `test_result_fk_idx` (`test_result_fk` ASC) VISIBLE,
  CONSTRAINT `test_result_fk`
    FOREIGN KEY (`test_result_fk`)
    REFERENCES `reportcard`.`test_result` (`test_result_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `reportcard`.`test_case`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`test_case` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`test_case` (
  `test_case_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `test_suite_fk` BIGINT UNSIGNED NOT NULL,
  `test_status_fk` TINYINT NOT NULL,
  `name` VARCHAR(1024) NOT NULL,
  `class_name` VARCHAR(1024) NULL DEFAULT NULL,
  `time` DECIMAL(9,3) NULL DEFAULT NULL,
  `system_out` TEXT NULL DEFAULT NULL,
  `system_err` TEXT NULL DEFAULT NULL,
  `assertions` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`test_case_id`),
  INDEX `fk_test_case_status_idx` (`test_status_fk` ASC) VISIBLE,
  INDEX `fk_test_case_test_suite_idx` (`test_suite_fk` ASC) VISIBLE,
  CONSTRAINT `fk_test_case_test_status`
    FOREIGN KEY (`test_status_fk`)
    REFERENCES `reportcard`.`test_status` (`test_status_id`),
  CONSTRAINT `fk_test_case_test_suite`
    FOREIGN KEY (`test_suite_fk`)
    REFERENCES `reportcard`.`test_suite` (`test_suite_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `reportcard`.`fault_context`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`fault_context` ;

CREATE TABLE `fault_context` (
  `fault_context_id` tinyint NOT NULL AUTO_INCREMENT,
  `fault_context_name` varchar(64) NOT NULL,
  PRIMARY KEY (`fault_context_id`)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


-- -----------------------------------------------------
-- Table `reportcard`.`test_case_fault`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`test_case_fault` ;

CREATE TABLE `test_case_fault` (
    `test_case_fault_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `test_case_fk` bigint unsigned NOT NULL,
    `fault_context_fk` tinyint NOT NULL,
    `type` varchar(1024) DEFAULT NULL,
    `message` varchar(1024) DEFAULT NULL,
    `value` mediumtext,
    PRIMARY KEY (`test_case_fault_id`),
    KEY `fault_context_fk_idx` (`fault_context_fk`),
    KEY `test_case_fk_idx` (`test_case_fk`),
    CONSTRAINT `fk_test_case` FOREIGN KEY (`test_case_fk`) REFERENCES `test_case` (`test_case_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_fault_context` FOREIGN KEY (`fault_context_fk`) REFERENCES `fault_context` (`fault_context_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
