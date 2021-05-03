-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
SHOW WARNINGS;
-- -----------------------------------------------------
-- Schema reportcard
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `reportcard`;

-- -----------------------------------------------------
-- Schema reportcard
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reportcard` DEFAULT CHARACTER SET utf8;
SHOW WARNINGS;
USE `reportcard`;

-- -----------------------------------------------------
-- Table `org`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `org`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `org`
(
    `org_id`   INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `org_name` VARCHAR(255)     NOT NULL DEFAULT '''',
    PRIMARY KEY (`org_id`),
    UNIQUE INDEX `org_name_idx` (`org_name` ASC) 
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `repo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repo`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `repo`
(
    `repo_id`   INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `repo_name` VARCHAR(255)     NOT NULL,
    `org_fk`    INT(10) UNSIGNED NOT NULL,
    PRIMARY KEY (`repo_id`),
    UNIQUE INDEX `repo_name_idx` (`org_fk` ASC, `repo_name` ASC) ,
    INDEX `org_idx` (`org_fk` ASC) ,
    CONSTRAINT `repo_org_fk`
        FOREIGN KEY (`org_fk`)
            REFERENCES `org` (`org_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `branch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `branch`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `branch`
(
    `branch_id`   INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `branch_name` VARCHAR(255)     NOT NULL,
    `repo_fk`     INT(10) UNSIGNED NOT NULL,
    PRIMARY KEY (`branch_id`),
    UNIQUE INDEX `repo_branch_name_idx` (`repo_fk` ASC, `branch_name` ASC) ,
    INDEX `branch_repo_idx` (`repo_fk` ASC) ,
    CONSTRAINT `branch_repo_fk`
        FOREIGN KEY (`repo_fk`)
            REFERENCES `repo` (`repo_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `sha`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sha`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `sha`
(
    `sha_id`      BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `sha`         VARCHAR(255)        NOT NULL,
    `sha_created` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `branch_fk`   INT(10) UNSIGNED    NOT NULL,
    PRIMARY KEY (`sha_id`),
    INDEX `build_created` (`sha_created` ASC) ,
    INDEX `sha_branch_fk_idx` (`branch_fk` ASC) ,
    CONSTRAINT `sha_branch_fk`
        FOREIGN KEY (`branch_fk`)
            REFERENCES `branch` (`branch_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `context`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `context`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `context`
(
    `context_id`  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `sha_fk`      BIGINT(20) UNSIGNED NOT NULL,
    `host`        VARCHAR(255)        NOT NULL,
    `application` VARCHAR(255)        NULL DEFAULT NULL,
    `pipeline`    VARCHAR(255)        NULL DEFAULT NULL,
    PRIMARY KEY (`context_id`),
    INDEX `context_sha_fk` (`sha_fk` ASC) ,
    CONSTRAINT `context_sha_fk`
        FOREIGN KEY (`sha_fk`)
            REFERENCES `sha` (`sha_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `execution`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `execution`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `execution`
(
    `execution_id`          BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `execution_external_id` VARCHAR(255)        NOT NULL,
    `context_fk`            BIGINT(20) UNSIGNED NOT NULL,
    PRIMARY KEY (`execution_id`),
    UNIQUE INDEX `execution_id_UNIQUE` (`execution_id` ASC) ,
    INDEX `execution_context_fk_idx` (`context_fk` ASC) ,
    CONSTRAINT `execution_context_fk`
        FOREIGN KEY (`context_fk`)
            REFERENCES `context` (`context_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `stage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `stage`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `stage`
(
    `stage_id`     BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `stage_name`   VARCHAR(255)        NOT NULL,
    `execution_fk` BIGINT(20) UNSIGNED NOT NULL,
    PRIMARY KEY (`stage_id`),
    UNIQUE INDEX `stage_id_UNIQUE` (`stage_id` ASC) ,
    INDEX `stage_execution_fk_idx` (`execution_fk` ASC) ,
    CONSTRAINT `stage_execution_fk`
        FOREIGN KEY (`execution_fk`)
            REFERENCES `execution` (`execution_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `test_status`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `test_status`
(
    `test_status_id`   TINYINT(4)  NOT NULL AUTO_INCREMENT,
    `test_status_name` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`test_status_id`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_result`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `test_result`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `test_result`
(
    `test_result_id`      BIGINT(20) UNSIGNED    NOT NULL AUTO_INCREMENT,
    `stage_fk`            BIGINT(20) UNSIGNED    NOT NULL,
    `tests`               INT(10) UNSIGNED       NOT NULL,
    `skipped`             INT(10) UNSIGNED       NOT NULL,
    `error`               INT(10) UNSIGNED       NOT NULL,
    `failure`             INT(10) UNSIGNED       NOT NULL,
    `time`                DECIMAL(9, 3) UNSIGNED NOT NULL,
    `test_result_created` TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `external_links`      JSON                   NULL     DEFAULT NULL,
    `is_success`          TINYINT(4) GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
    `has_skip`            TINYINT(4) GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
    PRIMARY KEY (`test_result_id`),
    INDEX `test_result_stage_fk_idx` (`stage_fk` ASC) ,
    CONSTRAINT `test_result_stage_fk`
        FOREIGN KEY (`stage_fk`)
            REFERENCES `stage` (`stage_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_suite`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `test_suite`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `test_suite`
(
    `test_suite_id`  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `test_result_fk` BIGINT(20) UNSIGNED NOT NULL,
    `tests`          INT(11)             NOT NULL,
    `skipped`        INT(11)             NOT NULL,
    `error`          INT(11)             NOT NULL,
    `failure`        INT(11)             NOT NULL,
    `time`           DECIMAL(9, 3)       NOT NULL,
    `package`        VARCHAR(1024)       NULL DEFAULT NULL,
    `group`          VARCHAR(1024)       NULL DEFAULT NULL,
    `properties`     JSON                NULL DEFAULT NULL,
    `is_success`     TINYINT(4) GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
    `has_skip`       TINYINT(4) GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
    PRIMARY KEY (`test_suite_id`),
    INDEX `test_result_fk_idx` (`test_result_fk` ASC) ,
    CONSTRAINT `test_result_fk`
        FOREIGN KEY (`test_result_fk`)
            REFERENCES `test_result` (`test_result_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_case`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `test_case`;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `test_case`
(
    `test_case_id`   BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `test_suite_fk`  BIGINT(20) UNSIGNED NOT NULL,
    `name`           VARCHAR(1024)       NOT NULL,
    `class_name`     VARCHAR(4096)       NOT NULL,
    `time`           DECIMAL(9, 3)       NOT NULL,
    `test_status_fk` TINYINT(4)          NOT NULL,
    PRIMARY KEY (`test_case_id`),
    INDEX `fk_test_case_status_idx` (`test_status_fk` ASC) ,
    INDEX `fk_test_case_test_suite_idx` (`test_suite_fk` ASC) ,
    CONSTRAINT `fk_test_case_test_status`
        FOREIGN KEY (`test_status_fk`)
            REFERENCES `test_status` (`test_status_id`),
    CONSTRAINT `fk_test_case_test_suite`
        FOREIGN KEY (`test_suite_fk`)
            REFERENCES `test_suite` (`test_suite_id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0
    DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
