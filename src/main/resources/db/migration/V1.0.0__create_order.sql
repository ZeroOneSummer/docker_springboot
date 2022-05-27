use sf_mall;
create table t_order(
    id int auto_increment primary key comment 'ID',
    order_no varchar(20) not null comment '订单编号',
    amount decimal comment '订单金额',
    count int comment '数量',
    addr varchar(80) comment '收货地址',
    phone varchar(20) comment '手机号',
    create_date datetime default current_timestamp comment '创建时间'
) character set 'utf8' comment '订单表';
