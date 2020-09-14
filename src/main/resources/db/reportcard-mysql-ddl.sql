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

-- -----------------------------------------------------
-- Schema reportcard
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reportcard` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
SHOW WARNINGS;
USE `reportcard` ;

-- -----------------------------------------------------
-- Table `org`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `org` (
                                     `org_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `org_name` VARCHAR(255) NOT NULL DEFAULT '',
                                     PRIMARY KEY (`org_id`),
                                     UNIQUE INDEX `org_name_idx` (`org_name` ASC) VISIBLE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `repo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `repo` (
                                      `repo_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `repo_name` VARCHAR(255) NOT NULL,
                                      `org_fk` INT UNSIGNED NOT NULL,
                                      PRIMARY KEY (`repo_id`),
                                      UNIQUE INDEX `repo_name_idx` (`org_fk` ASC, `repo_name` ASC) VISIBLE,
                                      INDEX `org_idx` (`org_fk` ASC) VISIBLE,
                                      CONSTRAINT `repo_org_fk`
                                          FOREIGN KEY (`org_fk`)
                                              REFERENCES `org` (`org_id`)
                                              ON DELETE CASCADE
                                              ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `app`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `app` (
                                     `app_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `app_name` VARCHAR(255) NOT NULL,
                                     `repo_fk` INT UNSIGNED NOT NULL,
                                     PRIMARY KEY (`app_id`),
                                     UNIQUE INDEX `app_name_idx` (`repo_fk` ASC, `app_name` ASC) VISIBLE,
                                     INDEX `app_repo_idx` (`repo_fk` ASC) VISIBLE,
                                     CONSTRAINT `app_repo_fk`
                                         FOREIGN KEY (`repo_fk`)
                                             REFERENCES `repo` (`repo_id`)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `branch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `branch` (
                                        `branch_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                        `branch_name` VARCHAR(255) NOT NULL,
                                        `repo_fk` INT UNSIGNED NOT NULL,
                                        PRIMARY KEY (`branch_id`),
                                        UNIQUE INDEX `repo_branch_name_idx` (`repo_fk` ASC, `branch_name` ASC) VISIBLE,
                                        INDEX `branch_repo_idx` (`repo_fk` ASC) VISIBLE,
                                        CONSTRAINT `branch_repo_fk`
                                            FOREIGN KEY (`repo_fk`)
                                                REFERENCES `repo` (`repo_id`)
                                                ON DELETE CASCADE
                                                ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `app_branch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `app_branch` (
                                            `app_branch_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                            `app_fk` INT UNSIGNED NOT NULL,
                                            `branch_fk` INT UNSIGNED NOT NULL,
                                            PRIMARY KEY (`app_branch_id`),
                                            INDEX `app_branch_app_fk_idx` (`app_fk` ASC) VISIBLE,
                                            INDEX `app_branch_branch_fk_idx` (`branch_fk` ASC) VISIBLE,
                                            CONSTRAINT `app_branch_app_fk`
                                                FOREIGN KEY (`app_fk`)
                                                    REFERENCES `app` (`app_id`)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE,
                                            CONSTRAINT `app_branch_branch_fk`
                                                FOREIGN KEY (`branch_fk`)
                                                    REFERENCES `branch` (`branch_id`)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `build`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `build` (
                                       `build_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                       `app_branch_fk` INT UNSIGNED NOT NULL,
                                       `build_unique_string` VARCHAR(255) NOT NULL,
                                       `build_unique_string_short` VARCHAR(6) GENERATED ALWAYS AS (left(`build_unique_string`,6)) STORED,
                                       `build_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`build_id`),
                                       UNIQUE INDEX `build_unique_string_idx` USING BTREE (`app_branch_fk`, `build_unique_string`) VISIBLE,
                                       INDEX `app_idx` (`app_branch_fk` ASC) VISIBLE,
                                       INDEX `build_created` (`build_created` DESC) VISIBLE,
                                       CONSTRAINT `build_app_branch_fk`
                                           FOREIGN KEY (`app_branch_fk`)
                                               REFERENCES `app_branch` (`app_branch_id`)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `stage` (
                                       `stage_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                       `stage_name` VARCHAR(255) NOT NULL,
                                       `app_branch_fk` INT UNSIGNED NOT NULL,
                                       PRIMARY KEY (`stage_id`),
                                       UNIQUE INDEX `stage_name_idx` (`app_branch_fk` ASC, `stage_name` ASC) VISIBLE,
                                       INDEX `stage_app_branch_idx` (`app_branch_fk` ASC) VISIBLE,
                                       CONSTRAINT `stage_app_branch_fk`
                                           FOREIGN KEY (`app_branch_fk`)
                                               REFERENCES `app_branch` (`app_branch_id`)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `build_stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `build_stage` (
                                             `build_stage_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                             `build_fk` BIGINT UNSIGNED NOT NULL,
                                             `stage_fk` INT UNSIGNED NOT NULL,
                                             PRIMARY KEY (`build_stage_id`),
                                             INDEX `build_idx` (`build_fk` ASC) VISIBLE,
                                             INDEX `build_stage_fk_stage_idx` (`stage_fk` ASC) VISIBLE,
                                             CONSTRAINT `fk_build`
                                                 FOREIGN KEY (`build_fk`)
                                                     REFERENCES `build` (`build_id`)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE,
                                             CONSTRAINT `fk_stage`
                                                 FOREIGN KEY (`stage_fk`)
                                                     REFERENCES `stage` (`stage_id`)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_status` (
                                             `test_status_id` TINYINT NOT NULL AUTO_INCREMENT,
                                             `test_status_name` CHAR(8) NOT NULL,
                                             PRIMARY KEY (`test_status_id`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 9
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_result`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_result` (
                                             `test_result_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                             `build_stage_fk` BIGINT UNSIGNED NOT NULL,
                                             `tests` INT UNSIGNED NOT NULL,
                                             `skipped` INT UNSIGNED NOT NULL,
                                             `error` INT UNSIGNED NOT NULL,
                                             `failure` INT UNSIGNED NOT NULL,
                                             `time` DECIMAL(9,3) UNSIGNED NOT NULL,
                                             `test_result_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             `is_success` TINYINT GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
                                             `has_skip` TINYINT GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
                                             PRIMARY KEY (`test_result_id`),
                                             INDEX `test_result_fk_build_stage_idx` (`build_stage_fk` ASC) VISIBLE,
                                             CONSTRAINT `fk_test_result_build_stage`
                                                 FOREIGN KEY (`build_stage_fk`)
                                                     REFERENCES `build_stage` (`build_stage_id`)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_suite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_suite` (
                                            `test_suite_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                            `test_result_fk` BIGINT UNSIGNED NOT NULL,
                                            `package` VARCHAR(1024) NOT NULL,
                                            `tests` INT NOT NULL,
                                            `skipped` INT NOT NULL,
                                            `error` INT NOT NULL,
                                            `failure` INT NOT NULL,
                                            `time` DECIMAL(9,3) NOT NULL,
                                            `is_success` TINYINT GENERATED ALWAYS AS (((`failure` + `error`) = 0)) VIRTUAL,
                                            `has_skip` TINYINT GENERATED ALWAYS AS ((`skipped` > 0)) VIRTUAL,
                                            PRIMARY KEY (`test_suite_id`),
                                            INDEX `test_result_fk_idx` (`test_result_fk` ASC) VISIBLE,
                                            CONSTRAINT `test_result_fk`
                                                FOREIGN KEY (`test_result_fk`)
                                                    REFERENCES `test_result` (`test_result_id`)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `test_case`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test_case` (
                                           `test_case_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                           `test_suite_fk` BIGINT UNSIGNED NOT NULL,
                                           `test_case_name` VARCHAR(1024) NOT NULL,
                                           `class_name` VARCHAR(4096) NOT NULL,
                                           `time` DECIMAL(9,3) NOT NULL,
                                           `test_status_fk` TINYINT NOT NULL,
                                           PRIMARY KEY (`test_case_id`),
                                           INDEX `fk_test_case_status_idx` (`test_status_fk` ASC) VISIBLE,
                                           INDEX `fk_test_case_test_suite_idx` (`test_suite_fk` ASC) VISIBLE,
                                           CONSTRAINT `fk_test_case_test_status`
                                               FOREIGN KEY (`test_status_fk`)
                                                   REFERENCES `test_status` (`test_status_id`),
                                           CONSTRAINT `fk_test_case_test_suite`
                                               FOREIGN KEY (`test_suite_fk`)
                                                   REFERENCES `test_suite` (`test_suite_id`)
                                                   ON DELETE CASCADE
                                                   ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
