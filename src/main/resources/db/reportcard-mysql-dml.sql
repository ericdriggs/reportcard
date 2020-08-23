INSERT INTO `reportcard`.`org`
(`id`, `name`)
VALUES (1, 'default');

INSERT INTO `reportcard`.`repo`
(`id`, `name`, `org_fk`)
VALUES (1, 'default', 1);

INSERT INTO `reportcard`.`app`
(`id`, `name`, `branch_fk`)
VALUES (1, 'app1', 1);

INSERT INTO `reportcard`.`branch`
(`id`, `name`, `repo_fk`)
VALUES (1, 'master', '1');

INSERT INTO `reportcard`.`app_branch`
(`app_branch_id`, `app_fk`, `branch_fk`)
VALUES (1, 1, 1)


# INSERT INTO `reportcard`.`stage`
# (`id`, `name`, `app_fk`)
# VALUES (1, 'unit', 1);
#
# INSERT INTO `reportcard`.`build`
# (`id`, `app_fk`, `app_build_ordinal`)
# VALUES
# (1, 1, 1);
#
#
