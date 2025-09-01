create table tb_talker (
    id UUID not null,
    full_name character varying not null,
    profile_photo character varying,
    about_me text,
    birth_date date not null,
    currently_city character varying,
    currently_state character varying,
    email character varying not null,
    password character varying not null,
    created_at timestamp without time zone not null,
    updated_at timestamp without time zone not null
);

alter table tb_talker
    add constraint pk_tb_talker primary key (id);

alter table tb_talker
    add constraint uc_tb_talker_email unique (email);