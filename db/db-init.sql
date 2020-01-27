CREATE SCHEMA cr;


CREATE TABLE `cr`.`exchange_rates` (
  `exchange_rate_id` INT NOT NULL AUTO_INCREMENT,
  `from_currency` VARCHAR(3) NOT NULL,
  `to_currency` VARCHAR(3) NOT NULL,
  `rate` FLOAT NOT NULL,
  `validity_start_date` DATETIME NOT NULL,
  `validity_end_date` DATETIME NOT NULL DEFAULT '2099-12-31 23:59:59',
  PRIMARY KEY (`exchange_rate_id`));
