use sf_mall;
create table t_user(
    id int auto_increment primary key comment 'ID',
    name varchar(20) comment '姓名',
    age tinyint comment '年龄',
    sex char comment '性别',
    phone varchar(20) comment '手机号',
    create_date datetime default current_timestamp comment '创建时间'
) character set 'utf8' comment '用户表';
