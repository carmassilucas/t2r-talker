alter table tb_talker
    add talker_type_id bigint not null default 1;

alter table tb_talker
    add constraint fk_tb_talker_on_talker_type foreign key (talker_type_id)
        references tb_talker_type (id);