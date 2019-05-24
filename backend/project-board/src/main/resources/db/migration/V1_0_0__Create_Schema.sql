-- MySQL8 specific schema generating script

/****************************************
*************** project *****************
****************************************/

create table if not exists project (
  id varchar(255) not null primary key,
  status varchar(255),
  issue_type varchar(255),
  title varchar(255),
  job text,
  skills text,
  description text,
  lob varchar(255),
  customer varchar(255),
  location varchar(255),
  operation_start varchar(255),
  operation_end varchar(255),
  effort varchar(255),
  created datetime(6),
  updated datetime(6),
  freelancer varchar(255),
  elongation varchar(255),
  other text,
  daily_rate varchar(255),
  travel_costs_compensated varchar(255)
);

/****************************************
************ project labels *************
****************************************/

create table if not exists project_labels (
  project_id varchar(255) not null,
  label varchar(255)
);

alter table project_labels
  add constraint fk_project_labels_project
  foreign key (project_id)
  references project(id);

/****************************************
***************** user ******************
****************************************/

create table if not exists pb_user (
  id varchar(255) not null primary key
);

/****************************************
************ user bookmarks *************
****************************************/

create table if not exists pb_user_bookmarks (
  user_id varchar(255) not null,
  project_id varchar(255) not null
);

alter table pb_user_bookmarks
  add constraint pk_pb_user_bookmarks
  primary key (user_id, project_id);

alter table pb_user_bookmarks
  add constraint fk_pb_user_bookmarks_pb_user
  foreign key (user_id)
  references pb_user(id);

alter table pb_user_bookmarks
  add constraint fk_pb_user_bookmarks_project
  foreign key (project_id)
  references project(id);

/****************************************
************** user data ****************
****************************************/

create table if not exists pb_user_data (
  id bigint not null auto_increment primary key,
  user_id varchar(255) not null unique,
  first_name varchar(255) not null,
  last_name varchar(255) not null,
  email varchar(255) not null,
  lob varchar(255),
  picture mediumblob,
  is_picture_initialized bit(1) not null
);

alter table pb_user_data
  add constraint fk_pb_user_data_pb_user
  foreign key (user_id)
  references pb_user(id);

/****************************************
********* project applications **********
****************************************/

create table if not exists project_application (
  id bigint not null auto_increment primary key,
  project_id varchar(255) not null,
  user_id varchar(255) not null,
  application_comment text,
  application_date datetime(6) not null
);

alter table project_application
  add constraint fk_project_application_project
  foreign key (project_id)
  references project(id);

alter table project_application
  add constraint fk_project_application_pb_user
  foreign key (user_id)
  references pb_user(id);

/****************************************
*********** access intervals ************
****************************************/

create table if not exists access_interval (
  id bigint not null auto_increment primary key,
  user_id varchar(255) not null,
  start_time datetime(6) not null,
  end_time datetime(6) not null
);

alter table access_interval
  add constraint fk_access_interval_pb_user
  foreign key (user_id)
  references pb_user(id);

/****************************************
********* hierarchy tree node ***********
****************************************/

create table if not exists hierarchy_tree_node (
 id bigint not null auto_increment primary key,
 manager_node_id bigint,
 user_id  varchar(255) not null unique,
 is_managing_user bit(1) not null
);

alter table hierarchy_tree_node
  add constraint fk_hierarchy_tree_node_hierarchy_tree_node
  foreign key (manager_node_id)
  references hierarchy_tree_node(id);

alter table hierarchy_tree_node
  add constraint fk_hierarchy_tree_node_pb_user
  foreign key (user_id)
  references pb_user(id);

/****************************************
****** hierarchy tree node staff ********
****************************************/

create table if not exists hierarchy_tree_node_all_staff (
  manager_node_id bigint not null,
  node_id bigint not null
);

alter table hierarchy_tree_node_all_staff
  add constraint fk_all_staff_manager_hierarchy_tree_node
  foreign key (manager_node_id)
  references hierarchy_tree_node(id);

alter table hierarchy_tree_node_all_staff
  add constraint  fk_all_staff_staff_hierarchy_tree_node
  foreign key (node_id)
  references hierarchy_tree_node(id);

/****************************************
********** scheduled job log ************
****************************************/

create table scheduled_job_log (
  id bigint not null auto_increment primary key,
  job_time datetime(6) not null,
  job_identifier varchar(255) not null,
  job_status tinyint not null
);

/****************************************
*********** template message ************
****************************************/

create table if not exists template_message (
  id bigint not null auto_increment primary key,
  ref_user_id varchar(255) not null,
  addressee_user_id varchar(255) not null,
  `text` text not null,
  `subject` varchar(255) not null
);

alter table template_message
  add constraint fk_template_message_ref_user_pb_user
  foreign key (ref_user_id)
  references pb_user(id);

alter table template_message
  add constraint fk_template_message_addressee_pb_user
  foreign key (addressee_user_id)
  references pb_user(id);

/****************************************
********** time aware message ***********
****************************************/

create table time_aware_message (
 id bigint not null primary key,
 relevancy_date_time datetime(6) not null
);

alter table time_aware_message
  add constraint fk_time_aware_message_template_message
  foreign key (id)
  references template_message(id);

/****************************************
************ simple message *************
****************************************/

create table simple_message (
  id bigint not null primary key
);

alter table simple_message
  add constraint fk_simple_message_template_message
  foreign key (id)
  references template_message(id);
