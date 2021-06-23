alter table confirmation_tokens
add constraint user_id_fk foreign key (user_id)
 references users(id) on delete cascade;

alter table recovery_password_tokens
add constraint user_id_fk foreign key (user_id)
 references users(id) on delete cascade;

alter table unique_vinyls_browsing_history
drop constraint user_id_fk,
add constraint user_id_fk foreign key (user_id)
 references users(id) on delete cascade;





