/****************************************
********* add is_offered column *********
****************************************/

alter table project_application
  add column is_offered bit(1);

update project_application
  set is_offered = false
  where is_offered is null;

alter table project_application
  modify column is_offered bit(1) not null;
