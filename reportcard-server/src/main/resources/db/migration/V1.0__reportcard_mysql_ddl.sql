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
-- Table `reportcard`.`org`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`org` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`org` (
  `org_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `org_name` VARCHAR(255) NOT NULL DEFAULT '''',
  PRIMARY KEY (`org_id`),
  UNIQUE INDEX `org_name_idx` (`org_name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


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
  PRIMARY KEY (`branch_id`),
  INDEX `branch_repo_idx` (`repo_fk` ASC) VISIBLE,
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
  `created` DATETIME NOT NULL DEFAULT (utc_timestamp()),
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
-- Table `reportcard`.`storage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportcard`.`storage` ;

CREATE TABLE IF NOT EXISTS `reportcard`.`storage` (
  `storage_id` BIGINT UNSIGNED NOT NULL,
  `stage_fk` BIGINT UNSIGNED NOT NULL,
  `path` VARCHAR(1024) NOT NULL,
  `indexFile` VARCHAR(1024) NULL DEFAULT NULL,
  `type` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`storage_id`),
  INDEX `stage_fk_idx` (`stage_fk` ASC) VISIBLE,
  CONSTRAINT `stage_fk`
    FOREIGN KEY (`stage_fk`)
    REFERENCES `reportcard`.`stage` (`stage_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


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
  `is_success` TINYINT(1) GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
  `has_skip` TINYINT(1) GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
  PRIMARY KEY (`test_result_id`),
  INDEX `test_result_stage_fk_idx` (`stage_fk` ASC) VISIBLE,
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
  `package` VARCHAR(1024) NULL DEFAULT NULL,
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


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
