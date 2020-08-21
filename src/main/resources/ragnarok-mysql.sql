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

-- -----------------------------------------------------
-- Schema reportcard
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reportcard` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `reportcard` ;

-- -----------------------------------------------------
-- Table `org`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `org` (
                                     `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `name` VARCHAR(255) NOT NULL DEFAULT '',
                                     PRIMARY KEY (`id`),
                                     UNIQUE INDEX `idx_org_name` (`name` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `repo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `repo` (
                                      `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `name` VARCHAR(255) NOT NULL,
                                      `org_fk` INT UNSIGNED NOT NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                      UNIQUE INDEX `org_repo_idx` (`id` ASC, `org_fk` ASC) VISIBLE,
                                      INDEX `org_idx` (`org_fk` ASC) VISIBLE,
                                      CONSTRAINT `org_fk`
                                          FOREIGN KEY (`org_fk`)
                                              REFERENCES `org` (`id`)
                                              ON DELETE CASCADE
                                              ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `branch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `branch` (
                                        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(255) NOT NULL,
                                        `repo_fk` INT UNSIGNED NOT NULL,
                                        PRIMARY KEY (`id`),
                                        UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                        UNIQUE INDEX `repo_branch_idx` (`repo_fk` ASC, `id` ASC) VISIBLE,
                                        INDEX `repo_idx` (`repo_fk` ASC) VISIBLE,
                                        CONSTRAINT `repo_fk`
                                            FOREIGN KEY (`repo_fk`)
                                                REFERENCES `repo` (`id`)
                                                ON DELETE CASCADE
                                                ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `app`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `app` (
                                     `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `name` VARCHAR(255) NOT NULL,
                                     `branch_fk` INT UNSIGNED NOT NULL,
                                     PRIMARY KEY (`id`),
                                     UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                     UNIQUE INDEX `branch_app_idx` (`branch_fk` ASC, `id` ASC) VISIBLE,
                                     INDEX `branch_idx` (`branch_fk` ASC) VISIBLE,
                                     CONSTRAINT `branch_fk`
                                         FOREIGN KEY (`branch_fk`)
                                             REFERENCES `branch` (`id`)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `build`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `build` (
                                       `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                       `app_fk` INT UNSIGNED NOT NULL,
                                       `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `app_build_ordinal` INT UNSIGNED NOT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE INDEX `app_build_idx` (`app_fk` ASC, `id` ASC) VISIBLE,
                                       INDEX `app_idx` (`app_fk` ASC) VISIBLE,
                                       CONSTRAINT `app_fk`
                                           FOREIGN KEY (`app_fk`)
                                               REFERENCES `app` (`id`)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `stage` (
                                       `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                       `name` VARCHAR(255) NOT NULL,
                                       `app_fk` INT UNSIGNED NOT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                       UNIQUE INDEX `app_stage_idx` (`app_fk` ASC, `id` ASC) VISIBLE,
                                       INDEX `app_idx` (`app_fk` ASC) VISIBLE,
                                       CONSTRAINT `stage_app_fk`
                                           FOREIGN KEY (`app_fk`)
                                               REFERENCES `app` (`id`)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `build_stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `build_stage` (
                                             `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                             `build_fk` BIGINT UNSIGNED NOT NULL,
                                             `stage_fk` INT UNSIGNED NOT NULL,
                                             PRIMARY KEY (`id`),
                                             INDEX `build_idx` (`build_fk` ASC) VISIBLE,
                                             INDEX `stage_fk_idx` (`stage_fk` ASC) VISIBLE,
                                             CONSTRAINT `fk_build`
                                                 FOREIGN KEY (`build_fk`)
                                                     REFERENCES `build` (`id`)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE,
                                             CONSTRAINT `fk_stage`
                                                 FOREIGN KEY (`stage_fk`)
                                                     REFERENCES `stage` (`id`)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `storage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `storage` (
                                         `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                         `build_stage_fk` BIGINT UNSIGNED NOT NULL,
                                         `label` VARCHAR(255) NOT NULL,
                                         `storage_type_fk` TINYINT NOT NULL,
                                         `s3_bucket` VARCHAR(63) NOT NULL,
                                         `s3_folder_path` VARCHAR(2048) NOT NULL,
                                         `s3_file_name` VARCHAR(8096) NULL DEFAULT NULL,
                                         `s3_file_matcher` VARCHAR(256) NULL DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         INDEX `build_stage_idx` (`build_stage_fk` ASC) VISIBLE,
                                         CONSTRAINT `fk_report_build_stage`
                                             FOREIGN KEY (`build_stage_fk`)
                                                 REFERENCES `build_stage` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `storage_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `storage_type` (
                                              `id` TINYINT NOT NULL AUTO_INCREMENT,
                                              `name` CHAR(16) NULL DEFAULT NULL,
                                              PRIMARY KEY (`id`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 13
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_status` (
                                             `id` TINYINT NOT NULL AUTO_INCREMENT,
                                             `name` CHAR(8) NOT NULL,
                                             PRIMARY KEY (`id`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 5
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test_result`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_result` (
                                             `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                             `build_stage_fk` BIGINT UNSIGNED NOT NULL,
                                             `tests` INT UNSIGNED NOT NULL,
                                             `skipped` INT UNSIGNED NOT NULL,
                                             `error` INT UNSIGNED NOT NULL,
                                             `failure` INT UNSIGNED NOT NULL,
                                             `time` DECIMAL(10,0) UNSIGNED NOT NULL,
                                             `is_success` TINYINT GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
                                             `has_skip` TINYINT GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
                                             PRIMARY KEY (`id`),
                                             INDEX `fk_test_result_build_stage_idx` (`build_stage_fk` ASC) VISIBLE,
                                             CONSTRAINT `fk_test_result_build_stage`
                                                 FOREIGN KEY (`build_stage_fk`)
                                                     REFERENCES `build_stage` (`id`)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test_suite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_suite` (
                                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                            `test_result_fk` BIGINT UNSIGNED NOT NULL,
                                            `package` VARCHAR(1024) NOT NULL,
                                            `tests` INT NOT NULL,
                                            `skipped` INT NOT NULL,
                                            `error` INT NOT NULL,
                                            `failure` INT NOT NULL,
                                            `time` DECIMAL(10,0) NOT NULL,
                                            `is_success` TINYINT GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
                                            `has_skip` TINYINT GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
                                            PRIMARY KEY (`id`),
                                            INDEX `test_result_fk_idx` (`test_result_fk` ASC) VISIBLE,
                                            CONSTRAINT `test_result_fk`
                                                FOREIGN KEY (`test_result_fk`)
                                                    REFERENCES `test_result` (`id`)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test_case`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_case` (
                                           `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                           `test_suite_fk` BIGINT UNSIGNED NOT NULL,
                                           `name` VARCHAR(1024) NOT NULL,
                                           `class_name` VARCHAR(8096) NOT NULL,
                                           `time` DECIMAL(10,0) NOT NULL,
                                           `status_fk` TINYINT NOT NULL,
                                           PRIMARY KEY (`id`),
                                           INDEX `fk_test_case_status_idx` (`status_fk` ASC) VISIBLE,
                                           INDEX `fk_test_case_test_suite_idx` (`test_suite_fk` ASC) VISIBLE,
                                           CONSTRAINT `fk_test_case_test_status`
                                               FOREIGN KEY (`status_fk`)
                                                   REFERENCES `test_status` (`id`),
                                           CONSTRAINT `fk_test_case_test_suite`
                                               FOREIGN KEY (`test_suite_fk`)
                                                   REFERENCES `test_suite` (`id`)
                                                   ON DELETE CASCADE
                                                   ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
