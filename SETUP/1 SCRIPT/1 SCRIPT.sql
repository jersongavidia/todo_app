
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema todo_app
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `todo_app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `mydb` ;

-- -----------------------------------------------------

USE `todo_app` ;

-- -----------------------------------------------------
-- Table `todo_app`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `todo_app`.`users` ;

CREATE TABLE IF NOT EXISTS `todo_app`.`users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `todo_app`.`tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `todo_app`.`tasks` ;

CREATE TABLE IF NOT EXISTS `todo_app`.`tasks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `completed` TINYINT(1) NULL DEFAULT '0',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `todo_app`.`users` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Insert sample users
INSERT INTO users (username, email, password) VALUES
('john_doe', 'john.doe@example.com', '$2a$10$D9zM4GvRNo56vZHVtvnpaO8.lLKN4p1y5FcTLKgYuvZDN2Nxx8k5W'), -- Password: "password123"
('jane_smith', 'jane.smith@example.com', '$2a$10$R5IeYPq8TSTG6iVlpHzUuOpNo2G/51Qm0vGlBQsL20Xa4K1.2GhQW'); -- Password: "123secure"

-- Insert sample tasks for the first user (john_doe)
INSERT INTO tasks (title, description, completed, user_id) VALUES
('Buy groceries', 'Milk, eggs, bread, and coffee', FALSE, 1),
('Complete project report', 'Finalize the report by end of the day', FALSE, 1),
('Read book', 'Finish reading chapter 5 of "Clean Code"', TRUE, 1);

-- Insert sample tasks for the second user (jane_smith)
INSERT INTO tasks (title, description, completed, user_id) VALUES
('Plan vacation', 'Research destinations and book flights', FALSE, 2),
('Workout', 'Morning yoga and strength training', TRUE, 2);

