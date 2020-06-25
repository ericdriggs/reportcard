-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema ragnarok
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema ragnarok
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ragnarok` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `ragnarok` ;

-- -----------------------------------------------------
-- Table `ragnarok`.`org`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`org` (
                                                `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                `name` VARCHAR(255) NOT NULL DEFAULT '',
                                                PRIMARY KEY (`id`),
                                                UNIQUE INDEX `idx_org_name` (`name` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`repo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`repo` (
                                                 `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                 `name` VARCHAR(255) NOT NULL,
                                                 `org_fk` INT UNSIGNED NOT NULL,
                                                 PRIMARY KEY (`id`),
                                                 UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                                 UNIQUE INDEX `org_repo_idx` (`id` ASC, `org_fk` ASC) VISIBLE,
                                                 INDEX `org_idx` (`org_fk` ASC) VISIBLE,
                                                 CONSTRAINT `org_fk`
                                                     FOREIGN KEY (`org_fk`)
                                                         REFERENCES `ragnarok`.`org` (`id`)
                                                         ON DELETE CASCADE
                                                         ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`branch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`branch` (
                                                   `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                   `name` VARCHAR(255) NOT NULL,
                                                   `repo_fk` INT UNSIGNED NOT NULL,
                                                   PRIMARY KEY (`id`),
                                                   UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                                   UNIQUE INDEX `repo_branch_idx` (`repo_fk` ASC, `id` ASC) VISIBLE,
                                                   INDEX `repo_idx` (`repo_fk` ASC) VISIBLE,
                                                   CONSTRAINT `repo_fk`
                                                       FOREIGN KEY (`repo_fk`)
                                                           REFERENCES `ragnarok`.`repo` (`id`)
                                                           ON DELETE CASCADE
                                                           ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`app`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`app` (
                                                `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                `name` VARCHAR(255) NOT NULL,
                                                `branch_fk` INT UNSIGNED NOT NULL,
                                                PRIMARY KEY (`id`),
                                                UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                                UNIQUE INDEX `branch_app_idx` (`branch_fk` ASC, `id` ASC) VISIBLE,
                                                INDEX `branch_idx` (`branch_fk` ASC) VISIBLE,
                                                CONSTRAINT `branch_fk`
                                                    FOREIGN KEY (`branch_fk`)
                                                        REFERENCES `ragnarok`.`branch` (`id`)
                                                        ON DELETE CASCADE
                                                        ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`build`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`build` (
                                                  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                  `app_fk` INT UNSIGNED NOT NULL,
                                                  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                  `app_build_ordinal` INT UNSIGNED NOT NULL,
                                                  PRIMARY KEY (`id`),
                                                  UNIQUE INDEX `app_build_idx` (`app_fk` ASC, `id` ASC) VISIBLE,
                                                  INDEX `app_idx` (`app_fk` ASC) VISIBLE,
                                                  CONSTRAINT `app_fk`
                                                      FOREIGN KEY (`app_fk`)
                                                          REFERENCES `ragnarok`.`app` (`id`)
                                                          ON DELETE CASCADE
                                                          ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`stage` (
                                                  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                  `name` VARCHAR(255) NOT NULL,
                                                  `app_fk` INT UNSIGNED NOT NULL,
                                                  PRIMARY KEY (`id`),
                                                  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                                  UNIQUE INDEX `app_stage_idx` (`app_fk` ASC, `id` ASC) VISIBLE,
                                                  INDEX `app_idx` (`app_fk` ASC) VISIBLE,
                                                  CONSTRAINT `stage_app_fk`
                                                      FOREIGN KEY (`app_fk`)
                                                          REFERENCES `ragnarok`.`app` (`id`)
                                                          ON DELETE CASCADE
                                                          ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`build_stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`build_stage` (
                                                        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                        `build_fk` INT UNSIGNED NOT NULL,
                                                        `stage_fk` INT UNSIGNED NOT NULL,
                                                        PRIMARY KEY (`id`),
                                                        INDEX `build_idx` (`build_fk` ASC) VISIBLE,
                                                        INDEX `stage_fk_idx` (`stage_fk` ASC) VISIBLE,
                                                        CONSTRAINT `build`
                                                            FOREIGN KEY (`build_fk`)
                                                                REFERENCES `ragnarok`.`build` (`id`)
                                                                ON DELETE CASCADE
                                                                ON UPDATE CASCADE,
                                                        CONSTRAINT `stage_fk`
                                                            FOREIGN KEY (`stage_fk`)
                                                                REFERENCES `ragnarok`.`stage` (`id`)
                                                                ON DELETE CASCADE
                                                                ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ragnarok`.`report`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ragnarok`.`report` (
                                                   `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                                   `label` VARCHAR(255) NOT NULL,
                                                   `type` ENUM('surefire_xml', 'junit_xml', 'xml', 'html') NOT NULL,
                                                   `build_stage_fk` INT UNSIGNED NOT NULL,
                                                   `files_fk` INT NOT NULL,
                                                   PRIMARY KEY (`id`),
                                                   UNIQUE INDEX `build_stage_report_idx` (`build_stage_fk` ASC, `id` ASC) VISIBLE,
                                                   INDEX `build_stage_idx` (`build_stage_fk` ASC) VISIBLE,
                                                   CONSTRAINT `build_stage_fk`
                                                       FOREIGN KEY (`build_stage_fk`)
                                                           REFERENCES `ragnarok`.`build_stage` (`id`)
                                                           ON DELETE CASCADE
                                                           ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
