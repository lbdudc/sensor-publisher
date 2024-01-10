/*% if (feature.MWM_TA_TrajectoryAnnotator || feature.UserManagement || feature.UM_AccountActivation || feature.T_FileUploader || feature.MV_MS_GJ_Cached) { %*/

--
-- CREATE QUARTZ TABLES
--

CREATE TABLE IF NOT EXISTS qrtz_job_details
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE BOOL NOT NULL,
    IS_NONCONCURRENT BOOL NOT NULL,
    IS_UPDATE_DATA BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
	REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_simple_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
	REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_cron_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
	REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_simprop_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 BOOL NULL,
    BOOL_PROP_2 BOOL NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_blob_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BYTEA NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_calendars
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BYTEA NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);


CREATE TABLE IF NOT EXISTS qrtz_paused_trigger_grps
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE IF NOT EXISTS qrtz_fired_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    SCHED_TIME BIGINT NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE IF NOT EXISTS qrtz_scheduler_state
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE IF NOT EXISTS qrtz_locks
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

create index IF NOT EXISTS idx_qrtz_j_req_recovery on qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index IF NOT EXISTS idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index IF NOT EXISTS idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index IF NOT EXISTS idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index IF NOT EXISTS idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index IF NOT EXISTS idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index IF NOT EXISTS idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index IF NOT EXISTS idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index IF NOT EXISTS idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index IF NOT EXISTS idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index IF NOT EXISTS idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index IF NOT EXISTS idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index IF NOT EXISTS idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index IF NOT EXISTS idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index IF NOT EXISTS idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index IF NOT EXISTS idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index IF NOT EXISTS idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index IF NOT EXISTS idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index IF NOT EXISTS idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index IF NOT EXISTS idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);

-- Remove data from Quartz tables to prevent errors

TRUNCATE TABLE qrtz_blob_triggers, qrtz_calendars, qrtz_cron_triggers,
  qrtz_fired_triggers, qrtz_job_details, qrtz_paused_trigger_grps, qrtz_scheduler_state,
  qrtz_simple_triggers, qrtz_simprop_triggers, qrtz_triggers, qrtz_locks;

/*% } %*/
-- Insert default data
SELECT 1;
/*% if (feature.UserManagement) { %*/
INSERT INTO um_authority (name) SELECT 'ROLE_USER'  WHERE NOT EXISTS (SELECT 1 FROM um_authority WHERE name LIKE 'ROLE_USER');
INSERT INTO um_authority (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM um_authority WHERE name LIKE 'ROLE_ADMIN');
/*% } %*/
/*% if (feature.MapViewer) { %*/
CREATE EXTENSION IF NOT EXISTS postgis;
/*% } %*/
/*%
    if (feature.GUI_StaticPages && data.statics) {
       data.statics.forEach(el => { %*/
INSERT INTO static_page (id, defined_id, created_date, modified_date)
    SELECT nextval('static_page_id_seq'), '/*%= el.id %*/', CURRENT_DATE, CURRENT_DATE
    WHERE NOT EXISTS (SELECT defined_id FROM static_page WHERE defined_id LIKE '/*%= el.id %*/');

INSERT INTO static_page_i18n (id, body, language, static_page_id)
    SELECT nextval('static_page_i18n_id_seq'), '/*%= el.html.replace(/'/g, "''") %*/', language.key, sp.id
    FROM static_page sp, (VALUES
    /*% for (var index = 0; index < (data.basicData.languages.length - 1); index++) {  %*/
    ('/*%= data.basicData.languages[index].toLocaleUpperCase() %*/'),
    /*% } %*/
    ('/*%= data.basicData.languages[index].toLocaleUpperCase() %*/')) AS language(key)
    WHERE sp.defined_id LIKE '/*%= el.id %*/'
    AND NOT EXISTS (SELECT * FROM static_page_i18n WHERE static_page_id = sp.id);
   /*% })
   } %*/


-- TRAJECTORY EXPLOITATION DATA --


/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
delete from public.ta_taxonomy_entity_employees;
delete from public.ta_taxonomy_entity;
delete from public.ta_segment_fusion_locations;
delete from public.ta_segment_fusion;
/*% } %*/
/*% if (feature.MWM_TrajectoryAnnotation) { %*/
delete from public.ta_location;
delete from public.ta_segment;
delete from public.ta_tag;
/*% } %*/
/*% if (feature.MWM_TrajectoryExploitation) { %*/
delete from public.te_activity;
delete from public.te_activity_category;
delete from public.te_planned_event;
delete from public.te_client;
delete from public.te_employee;

--
-- Data for Name: te_client; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (5, 'Polígono Industrial de Covas, 14, 15863 Negreira, C', 'contacto@andasa.com', 'Andasa SAU Negreira', '0101000020E61000004C3448C1537821C00B96EA025E744540', '981734256', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (6, 'Rúa A Camuza B, 2, 15113 Malpica, C', 'contacto@malpic.com', 'Malpic SC Malpica', '0101000020E6100000DACBB6D3D6A021C04A6249B9FBA84540', '981226587', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (7, 'Lugar Lago, 5, 15123 Camariñas, C', 'contacto@compso.com', 'Compso SL Camariñas', '0101000020E6100000679AB0FD645C22C0B075A911FA914540', '981465552', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (8, 'Rúa Mariña, 22, 15125 Muxía, A Coruña', 'contacto@vaquitas.com', 'Vaquitas SA Muxía', '0101000020E610000082E7DEC3256F22C059C345EEE98C4540', '981764223', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (9, 'Praza do Concello, s/n, 15145 A Laracha, C', 'contacto@cochos.com', 'Cochos SC A Laracha', '0101000020E61000002BC24D46952921C0B7F1272A1BA04540', '981333452', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (10, 'Rúa Virrey Osorio, 30, 15011 A Coruña','serverino.fuente@outlook.es', 'Severino Fuente Herrera', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', '659874445', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (11, 'Rúa Rioxa, 14, 15002 A Coruña','raul.guerrero@terra.net', 'Raul Guerrero Soto', '0101000020E61000004510E7E104CE20C0FA298E03AFB04540', '610061234', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (12, 'Rúa Filantropía, 3, 15011 A Coruña','paula.gzl@gmail.com', 'Paula Gonzalez Rubio', '0101000020E61000007C293C6876D520C0D4F19881CAAE4540', '764125948', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (13, 'Rúa Alfonso Senra, 120, 15680 Ordes, A Coruña','contacto@overjas.com', 'Ovejas SAU Ordes', '0101000020E6100000588FFB56EBD420C00806103E948A4540', '981001025', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (14, 'Av. de Compostela, 30, 15992 Boiro, C','contacto@terneros.com', 'Terneros SL Boiro', '0101000020E610000063D520CCEDC621C045F5D6C056534540', '981460580', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (15, 'Av. Malecón, 43, 15960 Ribeira, Galicia, C','contacto@caballos.com', 'Caballos SC Ribeira', '0101000020E6100000D525E318C9FE21C083C2A04CA3474540', '981040202', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (1, 'Praza de Lugo, s/n, 15004 A Coruña', 'mateo.alfaya@gmail.com', 'Mateo Alfaya Rodriguez', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', '618431256', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (2, 'Av. de Arteixo, 171, 15007 A Coruña, C', 'daniela.hnz@hotmail.com', 'Daniela Hernandez Casado', '0101000020E6100000F1BC546CCCD320C07825C9737DAD4540', '644532669', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (3, 'Av. Malpica, 125, 15100 Carballo, C', 'contacto@anel.com', 'Anel SL Carballo', '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', '981346285', true);
INSERT INTO public.te_client (id, address, email, full_name, location, phone, active) VALUES (4, 'Av. da Peregrina, 16, 15220 Bertamiráns, C', 'contacto@rapsal.com', 'Rapsal SA Bertamiráns', '0101000020E6100000026729594E5221C0239F573CF56E4540', '981000045', true);


--
-- Name: te_client_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.te_client_id_seq', 15);

--
-- Data for Name: te_employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.te_employee (id, email, full_name, location, phone, active) VALUES (1, 'email1', 'Hugo Fernandez Pereira', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', '654786153', true);
INSERT INTO public.te_employee (id, email, full_name, location, phone, active) VALUES (2, 'email2', 'Alba Martinez Campos', '0101000020E61000002B16BF29AC6C21C0B2497EC4AF9C4540', '685741235', true);
INSERT INTO public.te_employee (id, email, full_name, location, phone, active) VALUES (3, 'email3', 'Martin Rodriguez Lamela', '0101000020E61000009A79724D810421C07C4276DEC6724540', '678153264', true);
INSERT INTO public.te_employee (id, email, full_name, location, phone, active) VALUES (4, 'email4', 'Julia Vazquez Varela', NULL, '694152394', true);
INSERT INTO public.te_employee (id, email, full_name, location, phone, active) VALUES (5, 'email5', 'Maria Teresa Pena Mendez', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', '699276134', true);

--
-- Name: te_employee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.te_employee_id_seq', 5);

/*% if (feature.MVM_VT_WithTime) { %*/
--
-- Data for Name: te_planned_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (1, 'Praza de Lugo, s/n, 15004 A Coruña', 'Atender a cliente Plaza de Lugo', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', 1, '2019-11-13', '09:30', '07:30', 1, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (2, 'Av. de Arteixo, 171, 15007 A Coruña, C', 'Atender a cliente Av. de Arteixo', '0101000020E6100000F1BC546CCCD320C07825C9737DAD4540', 1, '2019-11-13', '11:30', '10:10', 2, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (3, 'Av. Malpica, 125, 15100 Carballo, C', 'Recoger animal en Carballo', '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', 1, '2019-11-14', '12:30', '12:00', 3, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (4, 'Av. da Peregrina, 16, 15220 Bertamiráns, C', 'Recoger animal en Bertamiráns', '0101000020E6100000026729594E5221C0239F573CF56E4540', 1, '2019-11-14', '08:30', '08:00', 4, 3);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (5, 'Polígono Industrial de Covas, 14, 15863 Negreira, C', 'Recoger animal en Negreira', '0101000020E61000004C3448C1537821C00B96EA025E744540', 1, '2019-11-14', '09:30', '09:00', 5, 3);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (6, 'Rúa A Camuza B, 2, 15113 Malpica, C', 'Recoger animal en Malpica', '0101000020E6100000DACBB6D3D6A021C04A6249B9FBA84540', 1, '2019-11-18', '11:30', '11:00', 6, 4);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (7, 'Lugar Lago, 5, 15123 Camariñas, C', 'Recoger animal en Camariñas', '0101000020E6100000679AB0FD645C22C0B075A911FA914540', 0, '2019-11-20', '16:00', '15:30', 7, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (8, 'Rúa Mariña, 22, 15125 Muxía, A Coruña', 'Recoger animal en Muxía', '0101000020E610000082E7DEC3256F22C059C345EEE98C4540', 0, '2019-11-20', '17:15', '16:45', 8, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (9, 'Praza do Concello, s/n, 15145 A Laracha, C', 'Recoger animal en A Laracha', '0101000020E61000002BC24D46952921C0B7F1272A1BA04540', 0, '2019-11-21', '10:30', '10:00', 9, 4);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (10, 'Rúa Virrey Osorio, 30, 15011 A Coruña', 'Atender a cliente C/ Vicarrey Osorio', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', 0, '2019-11-20', '12:30', '10:30', 10, 5);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (11, 'Rúa Rioxa, 14, 15002 A Coruña', 'Atender a cliente C/ A Rioxa', '0101000020E61000004510E7E104CE20C0FA298E03AFB04540', 0, '2019-11-20', '15:30', '14:30', 11, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (12, 'Rúa Filantropía, 3, 15011 A Coruña', 'Atender a cliente C/ Filantropía', '0101000020E61000007C293C6876D520C0D4F19881CAAE4540', 0, '2019-11-20', '15:00', '13:00', 12, 5);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (13, 'Rúa Alfonso Senra, 120, 15680 Ordes, A Coruña', 'Recoger animal en Ordes', '0101000020E6100000588FFB56EBD420C00806103E948A4540', 0, '2019-11-22', '15:30', '15:00', 13, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (14, 'Av. de Compostela, 30, 15992 Boiro, C', 'Recoger animal en Boiro', '0101000020E610000063D520CCEDC621C045F5D6C056534540', 0, '2019-11-21', '09:00', '08:30', 14, 4);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (15, 'Av. Malecón, 43, 15960 Ribeira, Galicia, C', 'Recoger animal en Ribeira', '0101000020E6100000D525E318C9FE21C083C2A04CA3474540', 0, '2019-11-21', '10:30', '10:00', 15, 4);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (16, 'Praza de Lugo, s/n, 15004 A Coruña', 'Atender a cliente Plaza de Lugo', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', 0, '2019-11-15', '14:30', '11:30', 1, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (17, 'Av. de Arteixo, 171, 15007 A Coruña, C', 'Atender a cliente Av. de Arteixo', '0101000020E6100000F1BC546CCCD320C07825C9737DAD4540', 0, '2019-11-14', '17:45', '17:00', 2, 5);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (18, 'Rúa Virrey Osorio, 30, 15011 A Coruña', 'Atender a cliente C/ Vicarrey Osorio', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', 0, '2019-11-14', '19:30', '18:15', 10, 5);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (19, 'Av. Malpica, 125, 15100 Carballo, C', 'Recoger animal en Carballo', '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', 0, '2019-11-19', '09:00', '08:30', 3, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (20, 'Rúa A Camuza B, 2, 15113 Malpica, C', 'Recoger animal en Malpica', '0101000020E6100000DACBB6D3D6A021C04A6249B9FBA84540', 0, '2019-11-19', '10:30', '10:00', 6, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (21, 'Av. de Compostela, 30, 15992 Boiro, C', 'Recoger animal en Boiro', '0101000020E610000063D520CCEDC621C045F5D6C056534540', 0, '2019-11-19', '10:45', '10:15', 14, 3);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (22, 'Av. Malecón, 43, 15960 Ribeira, Galicia, C', 'Recoger animal en Ribeira', '0101000020E6100000D525E318C9FE21C083C2A04CA3474540', 0, '2019-11-19', '11:50', '11:20', 15, 3);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (23, 'Rúa Virrey Osorio, 30, 15011 A Coruña', 'Atender a cliente C/ Vicarrey Osorio', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', 0, '2019-11-19', '14:00', '08:00', 11, 5);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (24, 'Rúa Rioxa, 14, 15002 A Coruña', 'Atender a cliente C/ A Rioxa', '0101000020E61000004510E7E104CE20C0FA298E03AFB04540', 0, '2019-11-19', '16:30', '16:00', 12, 5);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, end_time, start_time, client_id, employee_id) VALUES (25, 'Praza de Lugo, s/n, 15004 A Coruña', 'Atender a cliente Plaza de Lugo', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', 0, '2019-11-19', '10:45', '08:45', 1, 1);
/*% } %*/
/*% if (feature.MVM_VT_OnlyDay) { %*/
--
-- Data for Name: te_planned_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (1, 'Praza de Lugo, s/n, 15004 A Coruña', 'Atender a cliente Plaza de Lugo', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', 1, '2019-11-13', 1, 1, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (2, 'Av. de Arteixo, 171, 15007 A Coruña, C', 'Atender a cliente Av. de Arteixo', '0101000020E6100000F1BC546CCCD320C07825C9737DAD4540', 1, '2019-11-13', 2, 1, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (3, 'Av. Malpica, 125, 15100 Carballo, C', 'Recoger animal en Carballo', '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', 1, '2019-11-14', 3, 2, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (4, 'Av. da Peregrina, 16, 15220 Bertamiráns, C', 'Recoger animal en Bertamiráns', '0101000020E6100000026729594E5221C0239F573CF56E4540', 1, '2019-11-14', 4, 3, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (5, 'Polígono Industrial de Covas, 14, 15863 Negreira, C', 'Recoger animal en Negreira', '0101000020E61000004C3448C1537821C00B96EA025E744540', 1, '2019-11-14', 5, 3, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (6, 'Rúa A Camuza B, 2, 15113 Malpica, C', 'Recoger animal en Malpica', '0101000020E6100000DACBB6D3D6A021C04A6249B9FBA84540', 1, '2019-11-18', 6, 4, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (7, 'Lugar Lago, 5, 15123 Camariñas, C', 'Recoger animal en Camariñas', '0101000020E6100000679AB0FD645C22C0B075A911FA914540', 0, '2019-11-20', 7, 2, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (8, 'Rúa Mariña, 22, 15125 Muxía, A Coruña', 'Recoger animal en Muxía', '0101000020E610000082E7DEC3256F22C059C345EEE98C4540', 0, '2019-11-20', 8, 2, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (9, 'Praza do Concello, s/n, 15145 A Laracha, C', 'Recoger animal en A Laracha', '0101000020E61000002BC24D46952921C0B7F1272A1BA04540', 0, '2019-11-21', 9, 4, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (10, 'Rúa Virrey Osorio, 30, 15011 A Coruña', 'Atender a cliente C/ Vicarrey Osorio', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', 0, '2019-11-20', 10, 5, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (11, 'Rúa Rioxa, 14, 15002 A Coruña', 'Atender a cliente C/ A Rioxa', '0101000020E61000004510E7E104CE20C0FA298E03AFB04540', 0, '2019-11-20', 11, 1, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (12, 'Rúa Filantropía, 3, 15011 A Coruña', 'Atender a cliente C/ Filantropía', '0101000020E61000007C293C6876D520C0D4F19881CAAE4540', 0, '2019-11-20', 12, 5, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (13, 'Rúa Alfonso Senra, 120, 15680 Ordes, A Coruña', 'Recoger animal en Ordes', '0101000020E6100000588FFB56EBD420C00806103E948A4540', 0, '2019-11-22', 13, 1, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (14, 'Av. de Compostela, 30, 15992 Boiro, C', 'Recoger animal en Boiro', '0101000020E610000063D520CCEDC621C045F5D6C056534540', 0, '2019-11-21', 14, 4, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (15, 'Av. Malecón, 43, 15960 Ribeira, Galicia, C', 'Recoger animal en Ribeira', '0101000020E6100000D525E318C9FE21C083C2A04CA3474540', 0, '2019-11-21', 15, 4, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (16, 'Praza de Lugo, s/n, 15004 A Coruña', 'Atender a cliente Plaza de Lugo', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', 0, '2019-11-15', 1, 1, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (17, 'Av. de Arteixo, 171, 15007 A Coruña, C', 'Atender a cliente Av. de Arteixo', '0101000020E6100000F1BC546CCCD320C07825C9737DAD4540', 0, '2019-11-14', 2, 5, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (18, 'Rúa Virrey Osorio, 30, 15011 A Coruña', 'Atender a cliente C/ Vicarrey Osorio', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', 0, '2019-11-14', 10, 5, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (19, 'Av. Malpica, 125, 15100 Carballo, C', 'Recoger animal en Carballo', '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', 0, '2019-11-19', 3, 2, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (20, 'Rúa A Camuza B, 2, 15113 Malpica, C', 'Recoger animal en Malpica', '0101000020E6100000DACBB6D3D6A021C04A6249B9FBA84540', 0, '2019-11-19', 6, 2, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (21, 'Av. de Compostela, 30, 15992 Boiro, C', 'Recoger animal en Boiro', '0101000020E610000063D520CCEDC621C045F5D6C056534540', 0, '2019-11-19', 14, 3, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (22, 'Av. Malecón, 43, 15960 Ribeira, Galicia, C', 'Recoger animal en Ribeira', '0101000020E6100000D525E318C9FE21C083C2A04CA3474540', 0, '2019-11-19', 15, 3, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (23, 'Rúa Virrey Osorio, 30, 15011 A Coruña', 'Atender a cliente C/ Vicarrey Osorio', '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', 0, '2019-11-19', 11, 5, 1);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (24, 'Rúa Rioxa, 14, 15002 A Coruña', 'Atender a cliente C/ A Rioxa', '0101000020E61000004510E7E104CE20C0FA298E03AFB04540', 0, '2019-11-19', 12, 5, 2);
INSERT INTO public.te_planned_event (id, address, description, geom, state, date, client_id, employee_id, event_order) VALUES (25, 'Praza de Lugo, s/n, 15004 A Coruña', 'Atender a cliente Plaza de Lugo', '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', 0, '2019-11-19', 1, 1, 1);
/*% } %*/
--
-- Name: te_planned_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.te_planned_event_id_seq', 25, true);

--
-- Data for Name: te_activity_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.te_activity_category (id, label, color) VALUES (1, 'INACTIVO', '#4CFF33');
INSERT INTO public.te_activity_category (id, label, color) VALUES (2, 'ACTIVO', '#09F1EE');
INSERT INTO public.te_activity_category (id, label, color) VALUES (3, 'ANDANDO', '#C812D3');
INSERT INTO public.te_activity_category (id, label, color) VALUES (4, 'CONDUCIENDO', '#FF0080');

--
-- Name: te_activity_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.te_activity_category_id_seq', 4);

--
-- Data for Name: te_activity; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (1, '0105000020E610000001000000010200000010000000BEB64E2AEFD520C05CEAE78F8EAE454070BE1228D7D420C0F5449EAA3DAE454080C23CC463D320C0CD8B77698DAE45402D640E4839D220C0D04ABE638CAE454078E49CF51FD220C0F58CAC3B85AE4540D3E45A32FED120C0D05828DB8AAE454004397D5202D220C0A1109FB08EAE45405FAEE0442BD220C0C3A97FFD90AE454044AEF68536D220C03D946A0B88AE4540016F9567F4D120C0B3DD8D8887AE454007307D2696D120C0728B25087CAE45400C34A7C222D020C04BE05F7BA7AE454050F34D417ECF20C006CDE2A0B9AE454059F2FD49D8CF20C02D3EEEACD2AE4540AB3084D06ED020C0C7EDC34BC1AE454079F0471A81D020C0F2EF5868C6AE4540', to_timestamp(1573629600000/1000), to_timestamp(1573628400000/1000), 4, 1, 1);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (2, '0105000020E6100000010000000102000000070000007FD8C3CE7CD020C075467DE5C5AE4540B03834A16CD020C01DAB9FCEC1AE45408E1966BD1BD020C016FAA3A5CBAE454039DA8F3FDFCF20C09EF75BEED2AE4540B2B93EC80FD020C01CB134DCE0AE4540CD396E5707D020C0B44C34C1E1AE4540017AAA0DF5CF20C0B44C34C1E1AE4540', to_timestamp(1573630200000/1000), to_timestamp(1573629600000/1000), 3, 1, 1);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (3, '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', to_timestamp(1573637400000/1000), to_timestamp(1573630200000/1000), 2, 1, 1);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (4, '0105000020E6100000010000000102000000070000007FD8C3CE7CD020C075467DE5C5AE4540B03834A16CD020C01DAB9FCEC1AE45408E1966BD1BD020C016FAA3A5CBAE454039DA8F3FDFCF20C09EF75BEED2AE4540B2B93EC80FD020C01CB134DCE0AE4540CD396E5707D020C0B44C34C1E1AE4540017AAA0DF5CF20C0B44C34C1E1AE4540', to_timestamp(1573638000000/1000), to_timestamp(1573637400000/1000), 3, 1, 2);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (5, '0105000020E610000001000000010200000017000000F75E35B682D020C0040E7EAFC7AE45405ADD98D417D120C03E1B719FF2AE4540ECDC65383FD120C0866F2A11F0AE45407E5D3DC309D120C09F4D7A87C0AE4540B6DD5611F6D020C05FB183F8A6AE4540B6DD5611F6D020C04ACACFB884AE4540C9DDCB70F0D020C0A3AA634559AE4540485E2E9CC0D020C0920C16B63FAE45402AB50F3A3AD020C012E4E83729AE45401AB6EAD1E5CF20C0343E30AE10AE4540B6B67C8CADCF20C0AD24E718F6AD454090B692CDB8CF20C066BC0A61D5AD454035B6D470EBCF20C04E5EEE0798AD4540C6B5A1D412D020C0A468592770AD4540E9B43B9C61D020C07B8219B45BAD454070B343A9E8D020C068F1D1F539AD454052B209F34DD120C0313A5B013CAD454012B1701EC4D120C02742388052AD454007B0ABC723D220C07B8219B45BAD45407BAE3E75B0D220C0539464634DAD4540BB6B5C04ECD220C07896F4D255AD4540426A641173D320C0DD614AAC65AD45406569FED8C1D320C0875CE5BB51AD4540', to_timestamp(1573639200000/1000), to_timestamp(1573638000000/1000), 4, 1, 2);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (6, '0105000020E610000001000000010200000005000000C3F62BFEC2D320C075E02C9B51AD45409AD7E8BA77D320C079C69C7066AD454053162D7EECD320C0526DC0B572AD4540BD354417D1D320C015A445577EAD4540DB953F8AC6D320C0604083517DAD4540', to_timestamp(1573639800000/1000), to_timestamp(1573639200000/1000), 3, 1, 2);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (7, '0101000020E6100000F1BC546CCCD320C07825C9737DAD4540', to_timestamp(1573644600000/1000), to_timestamp(1573639800000/1000), 2, 1, 2);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (8, '0105000020E610000001000000010200000089000000ECE734D719D820C094A3C68F3EAD454084666C7398D820C02D544005E2AC4540696682B4A3D820C0580FE659DAAC45401068D9C50BD820C0C3174D1ACFAC4540E4E884CEBFD720C01D353A4CC1AC4540C1E9EA0671D720C0246F3CFBB2AC4540F4DB155174D720C06A11B038A7AC4540B25A37ACE7D720C0BE1498049EAC4540CA580C1D96D820C0B4C4F4A78DAC454057D67E6274D920C0041D63C87CAC45404153EA8D90DA20C0B6A5E74866AC45402AD055B9ACDB20C0503775464FAC4540205665A45DDD20C0C1D799752AAC45409BB757241DDE20C08D93D8B21EAC454002B3102DC3DF20C07F560AAD1DAC4540C6AFD769EDE020C013B4658416AC454080AC9EA617E220C0CDBD53D0F6AB45408EA9AEC025E320C03A69922DDAAB454091A5F98393E420C04A48F121D8AB4540267B59DF8AE520C010C5FBA1C1AB4540F578E57255E620C0F36384AD96AB4540FB76458409E720C0FD2271E773AB45400874559E17E820C0F61D2D8A63AB4540717123F503E920C07E49DFC340AB4540D570913A3CE920C0C1DE0DE619AB4540B271F772EDE820C0B6B20A08F3AA454034729F6EC0E820C06E64BB06C6AA4540D570913A3CE920C0EAB4BBED94AA45405B6F9947C3E920C0E9F7957653AA45404F6CBFA2DCEA20C0C32CCEBA32AA45409C69A33AD4EB20C040049D7DFBA94540E86537DB25ED20C02BB666F9B7A94540318123EAB9EE20C0492AE9D284A94540367F83FB6DEF20C0D97A15AC51A94540FE7D5F86DEEF20C063F4D84914A94540437C93157CF020C01F443F69EDA845404C7B431ED6F020C026A29D7CC4A84540667B2DDDCAF020C00E4D4FB3A1A84540497AF32630F120C061AE47C678A84540CF78FB33B7F120C00E4B800858A84540F27795FB05F220C0EB52755639A84540A77537D0DBF220C062584BA41AA845400874551E6EF320C02A77732D06A845402B73EFE5BCF320C0863D4B63E3A745406C73C363A6F320C08B2FFB98C0A74540A56E1DCE2AF320C081C442638BA745402DBC29DCEBF220C0779FE0A93FA74540FCBA7AC656F320C0F67440790BA7454046BAFE4C9AF320C0ED214F3CD5A6454018BA9F2EABF320C09EB101F39CA645402C73EFE516F420C0A1274BB566A6454071712375B4F420C0E99D3EAD39A64540E56EF1CBA0F520C0E9A91B1D21A645408F6C93A076F620C06230CE5C00A64540B7698D7979F720C0100E2A54D3A545404667458F5AF820C02974050F9CA545400AB88AA0EBF820C09C05ADED6AA5454091B692AD72F920C0864FD15050A5454004B460045FFA20C0B53505CC39A5454081B1A3BA45FB20C064E8AC9530A5454099AF782BF4FB20C088C5C0A132A545405DAD04BFBEFC20C0194FDEB335A5454075ABD92F6DFD20C0FA15E8B936A54540A1A9230016FE20C043C4847D2CA54540A7A78311CAFE20C0EF53E9221DA5454035A53B27ABFF20C0F85788EC13A54540DCA18D04DB0021C0F85788EC13A54540989FA438AB0121C01B2346C80DA54540EFACD142480221C0C9319B6DFEA445405BABEF90DA0221C0A80D2EB8DFA445400FAA56BC500321C0ECBD6AEABCA4454001A8416E0A0421C0BFF9E746A1A44540CAA61DF97A0421C01D5B3C9D84A44540B1553238E20421C0E661B7554DA44540D454CCFF300521C0167AFD4034A44540F053F167850521C0A436C85C23A4454075D2B3A4090621C0FE91897812A4454021D1A570850621C03BEEAF3507A445407BCF4E5F1D0721C0A55D2D8E00A445408EBA47CAE70721C0268C419401A4454012B73A89280921C0AED3F41918A44540A9B467FE030A21C06D2019CA35A4454025B2AAB4EA0A21C0B882051943A445402BAF456FFE0B21C0F77A62E239A445400AADBBC1BD0C21C01333EE6825A4454090ABC3CE440D21C0BAE27DA604A44540BFAA5D96930D21C04F0B80C5DEA3454098AA73D79E0D21C01B1D138FAAA34540FDA9E11CD70D21C0954A74A182A3454045AF2FEE5D0E21C05E9253A157A345406EAC29C7600F21C0D9EB357625A345406BAA14791A1021C00B2D1D5DF6A2454001A841EEF51021C0C987BB43C7A24540E1A4F2E9141221C09A43D01D96A2454038B21FD4491321C01714D8A757A2454073AF8E0C471421C0D2E1E7CA32A2454048AB7AB1C51521C0F2BFF11E16A2454055A88ACBD31621C0D9D82354F4A1454091A5F903D11721C0430DCCA1D6A145401FA3B119B21821C0CCC11E5ECBA145401D11F7D1B71A21C00C1AD847F2A14540710C3B7B631C21C0468CB51A40A245408708FB9DCB1D21C0278A1D1069A24540A1021BD2E71F21C0AF260DDF60A245407CFC66881A2221C035E8A4286DA2454010F87EAFAF2321C0FFFCB15975A245402BF29EE3CB2521C09A43D01D96A24540FDEC3A91A42721C0DFD04DC9B2A24540CFE7D63E7D2921C0262CD27FA6A24540EDE2466A3F2B21C02AB92B4171A24540BADF8206642C21C0009A0BD133A2454087DCBEA2882D21C058BC0EFEE5A14540E2D6B2548E2F21C09949915BA0A145403ED1A606943121C0D7C3C9336FA14540AEEF56F9353421C0E2D002FC10A14540D2E82636AC3621C09E8FD9A1D7A04540B4E122F5383921C07C9FDADBB6A04540CDDAF231AF3B21C07C9FDADBB6A04540AFD3EEF03B3E21C09FFB861DECA045400ACEE2A2414021C0169D4136F0A045401DC952CE034221C040225D70CFA0454041C2220B7A4421C0646B9CF4BAA0454099BBC6C5D94621C072BFA73671A04540F5B5BA77DF4821C05ED82A5F23A045400DAF8AB4554B21C07B024203EA9F4540E5ABC6507A4C21C06C3B2C4A779F4540EAA926622E4D21C059761EA2319F454005A446964A4F21C038EC4D0C199F4540E49BF25D315221C0545559DA109F454087941A9FD45421C007C1E71E469F4540F08B1E6BE85721C01D0384CD629F45403EDD3A9C4D5921C0DD10B282569F4540D4D9A2BA885A21C0227F6FAEDF9E454076B6A8EE0C5B21C0C3493682AE9E454032B4BF22DD5B21C0B35545B4B69E4540C1B23C8F5E5C21C07CA093ECBF9E4540B1B317270A5C21C0EA363025F39E4540', to_timestamp(1573732800000/1000), to_timestamp(1573729200000/1000), 4, 2, 3);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (9, '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', to_timestamp(1573734600000/1000), to_timestamp(1573732800000/1000), 2, 2, 3);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (10, '0105000020E610000001000000010200000089000000ECE734D719D820C094A3C68F3EAD454084666C7398D820C02D544005E2AC4540696682B4A3D820C0580FE659DAAC45401068D9C50BD820C0C3174D1ACFAC4540E4E884CEBFD720C01D353A4CC1AC4540C1E9EA0671D720C0246F3CFBB2AC4540F4DB155174D720C06A11B038A7AC4540B25A37ACE7D720C0BE1498049EAC4540CA580C1D96D820C0B4C4F4A78DAC454057D67E6274D920C0041D63C87CAC45404153EA8D90DA20C0B6A5E74866AC45402AD055B9ACDB20C0503775464FAC4540205665A45DDD20C0C1D799752AAC45409BB757241DDE20C08D93D8B21EAC454002B3102DC3DF20C07F560AAD1DAC4540C6AFD769EDE020C013B4658416AC454080AC9EA617E220C0CDBD53D0F6AB45408EA9AEC025E320C03A69922DDAAB454091A5F98393E420C04A48F121D8AB4540267B59DF8AE520C010C5FBA1C1AB4540F578E57255E620C0F36384AD96AB4540FB76458409E720C0FD2271E773AB45400874559E17E820C0F61D2D8A63AB4540717123F503E920C07E49DFC340AB4540D570913A3CE920C0C1DE0DE619AB4540B271F772EDE820C0B6B20A08F3AA454034729F6EC0E820C06E64BB06C6AA4540D570913A3CE920C0EAB4BBED94AA45405B6F9947C3E920C0E9F7957653AA45404F6CBFA2DCEA20C0C32CCEBA32AA45409C69A33AD4EB20C040049D7DFBA94540E86537DB25ED20C02BB666F9B7A94540318123EAB9EE20C0492AE9D284A94540367F83FB6DEF20C0D97A15AC51A94540FE7D5F86DEEF20C063F4D84914A94540437C93157CF020C01F443F69EDA845404C7B431ED6F020C026A29D7CC4A84540667B2DDDCAF020C00E4D4FB3A1A84540497AF32630F120C061AE47C678A84540CF78FB33B7F120C00E4B800858A84540F27795FB05F220C0EB52755639A84540A77537D0DBF220C062584BA41AA845400874551E6EF320C02A77732D06A845402B73EFE5BCF320C0863D4B63E3A745406C73C363A6F320C08B2FFB98C0A74540A56E1DCE2AF320C081C442638BA745402DBC29DCEBF220C0779FE0A93FA74540FCBA7AC656F320C0F67440790BA7454046BAFE4C9AF320C0ED214F3CD5A6454018BA9F2EABF320C09EB101F39CA645402C73EFE516F420C0A1274BB566A6454071712375B4F420C0E99D3EAD39A64540E56EF1CBA0F520C0E9A91B1D21A645408F6C93A076F620C06230CE5C00A64540B7698D7979F720C0100E2A54D3A545404667458F5AF820C02974050F9CA545400AB88AA0EBF820C09C05ADED6AA5454091B692AD72F920C0864FD15050A5454004B460045FFA20C0B53505CC39A5454081B1A3BA45FB20C064E8AC9530A5454099AF782BF4FB20C088C5C0A132A545405DAD04BFBEFC20C0194FDEB335A5454075ABD92F6DFD20C0FA15E8B936A54540A1A9230016FE20C043C4847D2CA54540A7A78311CAFE20C0EF53E9221DA5454035A53B27ABFF20C0F85788EC13A54540DCA18D04DB0021C0F85788EC13A54540989FA438AB0121C01B2346C80DA54540EFACD142480221C0C9319B6DFEA445405BABEF90DA0221C0A80D2EB8DFA445400FAA56BC500321C0ECBD6AEABCA4454001A8416E0A0421C0BFF9E746A1A44540CAA61DF97A0421C01D5B3C9D84A44540B1553238E20421C0E661B7554DA44540D454CCFF300521C0167AFD4034A44540F053F167850521C0A436C85C23A4454075D2B3A4090621C0FE91897812A4454021D1A570850621C03BEEAF3507A445407BCF4E5F1D0721C0A55D2D8E00A445408EBA47CAE70721C0268C419401A4454012B73A89280921C0AED3F41918A44540A9B467FE030A21C06D2019CA35A4454025B2AAB4EA0A21C0B882051943A445402BAF456FFE0B21C0F77A62E239A445400AADBBC1BD0C21C01333EE6825A4454090ABC3CE440D21C0BAE27DA604A44540BFAA5D96930D21C04F0B80C5DEA3454098AA73D79E0D21C01B1D138FAAA34540FDA9E11CD70D21C0954A74A182A3454045AF2FEE5D0E21C05E9253A157A345406EAC29C7600F21C0D9EB357625A345406BAA14791A1021C00B2D1D5DF6A2454001A841EEF51021C0C987BB43C7A24540E1A4F2E9141221C09A43D01D96A2454038B21FD4491321C01714D8A757A2454073AF8E0C471421C0D2E1E7CA32A2454048AB7AB1C51521C0F2BFF11E16A2454055A88ACBD31621C0D9D82354F4A1454091A5F903D11721C0430DCCA1D6A145401FA3B119B21821C0CCC11E5ECBA145401D11F7D1B71A21C00C1AD847F2A14540710C3B7B631C21C0468CB51A40A245408708FB9DCB1D21C0278A1D1069A24540A1021BD2E71F21C0AF260DDF60A245407CFC66881A2221C035E8A4286DA2454010F87EAFAF2321C0FFFCB15975A245402BF29EE3CB2521C09A43D01D96A24540FDEC3A91A42721C0DFD04DC9B2A24540CFE7D63E7D2921C0262CD27FA6A24540EDE2466A3F2B21C02AB92B4171A24540BADF8206642C21C0009A0BD133A2454087DCBEA2882D21C058BC0EFEE5A14540E2D6B2548E2F21C09949915BA0A145403ED1A606943121C0D7C3C9336FA14540AEEF56F9353421C0E2D002FC10A14540D2E82636AC3621C09E8FD9A1D7A04540B4E122F5383921C07C9FDADBB6A04540CDDAF231AF3B21C07C9FDADBB6A04540AFD3EEF03B3E21C09FFB861DECA045400ACEE2A2414021C0169D4136F0A045401DC952CE034221C040225D70CFA0454041C2220B7A4421C0646B9CF4BAA0454099BBC6C5D94621C072BFA73671A04540F5B5BA77DF4821C05ED82A5F23A045400DAF8AB4554B21C07B024203EA9F4540E5ABC6507A4C21C06C3B2C4A779F4540EAA926622E4D21C059761EA2319F454005A446964A4F21C038EC4D0C199F4540E49BF25D315221C0545559DA109F454087941A9FD45421C007C1E71E469F4540F08B1E6BE85721C01D0384CD629F45403EDD3A9C4D5921C0DD10B282569F4540D4D9A2BA885A21C0227F6FAEDF9E454076B6A8EE0C5B21C0C3493682AE9E454032B4BF22DD5B21C0B35545B4B69E4540C1B23C8F5E5C21C07CA093ECBF9E4540B1B317270A5C21C0EA363025F39E4540', to_timestamp(1573738200000/1000), to_timestamp(1573734600000/1000), 4, 2, NULL);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (11, '0105000020E61000000100000001020000003C00000029DCE97E5A0C21C07C0C90532675454067E07239D60A21C0C586E0B210754540C1E3205CA60921C0CF0A6549F17445404CE65205BA0821C075716EFED57445405BBC887AFB0621C05E7B8FA5A774454032BF8EA1F80521C0EF7CF5C967744540C0C17A0FD80421C0E12442821974454012BDBEB8830621C0D54C0C81B273454046C0821C5F0521C0A206E5492273454079C346803A0421C0CF47DF8BA2724540AAC5BAEC6F0321C002ECC1A43F72454000C24E8DC10421C091007BFAE4714540E2C6DE61FF0221C00F663940CC7145406CC636662C0321C0A33F86756D71454071C49677E00321C039D2626B067145400ABE0EB0290621C08DA00FE38E704540ACB636F1CC0821C0793081087270454028D5E6E38D0A21C010B0BFB6237045402ED346F5410B21C00919FE2AAC6F454061D60A591D0A21C06E1A9BC1176F454022D786D2D90921C0EC1EF5F6766E454035D2F6FD9B0B21C0C86B1961206E454015CAA2C5820E21C064E0B4AAC56D4540B7C2CA06261121C09F748932736D4540E3BA4A4CF61321C0F5988177396D454040B24E180A1721C02C2884182D6D4540A4ABF2D2691921C05FCC45F36A6D4540BCA4C20FE01B21C00F6125EDAC6D4540E89C4255B01E21C064E0B4AAC56D454079970A859F2021C0303F876BBD6D45400D07032B822321C02624F1ADA46D4540A6007B63CB2521C070A71E4F986D4540B9FBEA8E8D2721C047561E8BC16D45401AF43E52472A21C035770806F36D4540B6EE0682362C21C08F050422186E45401DE5BA56A42F21C0C863B5BF2C6E45407EDD0E1A5E3221C0FA664C1E396E4540A6D43E68883521C03660639C496E454007CD922B423821C04A656E55836E4540EAC58EEACE3A21C0A92867948B6E454054BD92B6E23D21C0760DEB74876E45406EB7B2EAFE3F21C0076E73167B6E4540C7B056A55E4221C0BD507559626E454022AB4A57644421C0CD89711A5A6E45407EA53E096A4621C0B960F678666E454013A15630FF4721C0EC1EF5F6766E4540C4B38C86354921C0C6D9F6B76E6E454038B21F34C24921C0BCE376986A6E454007B0ABC78C4A21C07D3613325D6E454076BC7239E64C21C02AFF8E411C6E45403BB93976104E21C0B3F69451616E454089B61D0E084F21C08E24ED299F6E4540B4B52C35514F21C0E0300184CC6E454066599EB7F64F21C0B7D28111DF6E4540DA573165835021C059E76F95DF6E454046564FB3155121C0772D1629E26E4540CD5457C09C5121C0C1D3887FED6E4540DD537C28F15121C0C23CC061FA6E454037D32F3E2C5221C051A2F50C006F4540C9D2FCA1535221C029D6D2A6F26E4540', to_timestamp(1573718400000/1000), to_timestamp(1573716600000/1000), 4, 3, 4);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (12, '0101000020E6100000026729594E5221C0239F573CF56E4540', to_timestamp(1573720200000/1000), to_timestamp(1573718400000/1000), 2, 3, 4);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (13, '0105000020E610000001000000010200000052000000FABE2F83535221C06EBC1B97F06E45404DBF7860375221C0B06D3FFDFD6E4540CCBED064645221C0D798825B0A6F454010C0F4D9F35121C0EF37ED7A0E6F4540BCBFABFC0F5221C0E6CAAD27256F45402AC0DE98E85121C048AABA8D326F454075C1776D725121C09FD51447296F45401DC3CE7EDA5021C0E3616137276F454038C3B83DCF5021C0CEEA1FAD366F4540B9C36039A25021C0A274EA514C6F45409BC22683075121C0C31B36A8576F454090C1612C675121C0ED6B8F54D36F454063C1020E785121C06C32FE4EF46F454017C06939EE5121C0E33F5E1A0F70454042BEB309975221C0C157EEAE22704540B6BC46B7235321C0B440E9D74970454042B3E48A945321C03480D29C857045401DB23575FF5321C0F2C5E4389A7045407EB053C3915421C00E84816FA170454004AF5BD0185521C0A730E75F9F704540B1AD4D9C945521C0E9EF130A9470454040ACCA08165621C04D8B1E1874704540BBA90DBFFC5621C01E0A5E1C3170454042A815CC835721C0C157EEAE227045406EA65F9C2C5821C0FF197A971F7045406BA44A4EE65821C095B3424B3770454038A186EA0A5A21C0393377E16C70454078B2F371BB5B21C0BAF54761C1704540A9AE9D53185D21C0C3BB1C9A0C714540FFABF64A0A5E21C08FF0ABC255714540EAB94050A75E21C01C51874FAC714540B9B8913A125F21C069940486197245400AB88A20505F21C0C326BBE247724540505DDE24935F21C0436ADB945F724540F25BD0F00E6021C064E035207272454082DA922D936021C0D0866F10817245400059E00A1D6121C02659F993927245403558EF31666121C0E577243EA972454099575D779E6121C05CBD7D9AC672454032DB9997D66121C0978AEBB8DB72454027DAD440366221C01CD1CE2CEB724540495864E1E16221C0C0E212A00B73454059578949366321C0A920BA971B73454061563952906321C0A206E54922734540F562EAF2F76321C006A0C368267345401AE2C98A496421C0E5060C6125734540FCE08FD4AE6421C0C1CCE1AE1E734540495E2E9CA36521C02DBE1415F9724540B55C4CEA356621C041C8B44BEF7245403B5B54F7BC6621C04E1E65B8EC724540545929686B6721C076DD8624FB72454034D7E48A2D6821C040259280187345403B558A6CE46821C08EFF8048447345404153EA7D986921C002EC5CF16B734540A1B4F2DE956A21C05D950D34A8734540D4B2B10E396B21C0C90D2C49CD73454096AE2854BD6C21C0DC3FB4B2FD73454037A75095606F21C088C76D3053744540C5E74AE8AD6F21C050DBFBE06A744540F567EFD69F6F21C0CB2DB41672744540746747DBCC6F21C0C4EC7E6C6C7445401FE6F3D6457021C09BBC9BBA65744540D6E5AAF9617021C009DA94A3627445405E65BD2D8C7021C03D55182F64744540F9E444C1B07021C0004B111861744540DCE30A0B167121C03957BE1F62744540BDE2D0547B7121C007D941AB63744540B2E10BFEDA7121C04F5A004B8A74454071602D594E7221C096B98D609E744540385F09E4BE7221C0FAC1E49DA674454009DE9F9E2C7321C0BAD3E4B4A974454007DD4FA7867321C05C9B6412A5744540BA5AACAB597421C04631D6CE8A744540B9D9A184B67421C0C2A5544389744540B758975D137521C069E8A83B887445401AD7FA7BA87521C0458CCFA0847445401655A05D5F7621C06D6D6F637C7445404053EA2D087721C09E1A0D2674744540F55151597E7721C031E465357674454055DA339FB07821C007D941AB63744540A8DA7C7C947821C0A7367647567445408F5B9DE4427821C0C5A42B665A744540', to_timestamp(1573722000000/1000), to_timestamp(1573720200000/1000), 4, 3, 5);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (14, '0101000020E61000004C3448C1537821C00B96EA025E744540', to_timestamp(1573723800000/1000), to_timestamp(1573722000000/1000), 2, 3, 5);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (15, '0101000020E61000004C3448C1537821C00B96EA025E744540', to_timestamp(1573725600000/1000), to_timestamp(1573723800000/1000), 1, 3, NULL);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (16, '0105000020E610000001000000010200000069000000B3B52C95407821C0C5A42B665A744540CFB451FD947821C0A7367647567445408EB47D7FAB7821C009DA94A362744540D1B6664BDB7721C09E1A0D267474454015B94F170B7721C031E4653576744540F7BC1A95A87521C0458CCFA084744540EABF0A7B9A7421C04631D6CE8A74454035C1A34F247421C09BD881D68B744540DCC2FA608C7321C0D39E8E77A174454004C5F96DC77221C0BAD3E4B4A9744540FFC6995C137221C0ED35363A9974454025C84872A87121C079D06A4C79744540A6C8F06D7B7121C007D941AB63744540E4CC7928F76F21C0E585E0845E74454065CEE67A6A6F21C0B7A612215174454078BDC2D0CD6A21C0EF133DAFB873454091C161CC546921C0D04BDFF95B734540CC6B17E7B56721C045B26A43FF724540646E4990C96621C04E1E65B8EC7245400D72B5EF776521C045B26A43FF724540A474E7988B6421C006A0C36826734540D777ABFC666321C006A0C368267345409F7C5112B06121C0CEFFF6FED3724540BC7D8BC84A6121C08AF0A10090724540C9AB224DCC5F21C0350A8ADA687245405DAD04FF395F21C0D88D457639724540E7AD215A075F21C0A2E0E08DF8714540C4AE8732915E21C0F02864478971454040B1CF1CB05D21C0DC9367F83A714540D7A832A7805C21C049071972E5704540B9ADC27BBE5A21C0F00F74D38C70454052B20973185921C05A91B4623A70454060B41EC15E5821C08DFF90BE2470454069AEC9B59F5721C0C157EEAE22704540ABB0B281CF5621C05A91B4623A70454060B41EE17D5521C02905AF199670454089B61DEEB85421C0C45EB467A070454083B98233A55321C0125CD8C38A70454023BB64E5125321C06CFDC6893F7045406C575BDB4F5221C08136022A117045408159E588905121C05A4E583FF26F4540DD59A3C56E5121C06363531BAA6F4540FB5ADD7B095121C008C18598556F4540D05C93AB605021C059A39AFB406F454062616543C04E21C0B5F5C2594D6F45408767198D8D4C21C0B5F5C2594D6F45402B6D25DB874A21C04582EE4E2A6F45408F725DAB984821C03A8B34EAD96E4540F678E5724F4621C002CB2C65856E4540A27DA1C9A34421C023B8A9CB4F6E454000857988004221C0F43FB00A586E4540038C930A7F3F21C02530F1357F6E4540539C253AA63921C0B12CA984896E4540A87B015B423521C016CB1B6D436E4540168703B62B3121C0C863B5BF2C6E4540C2918997582D21C0FD93C9311A6E454097710D74702921C0295E55A7E66D4540337869B9102721C050B3BA6E9C6D45400651D313232521C0A16C503F966D4540C259B906042221C0B11C8B1CB36D4540265FF1D6142021C03A94233CB76D4540F064E7E3031E21C0303F876BBD6D4540CC6B17A78D1B21C02624F1ADA46D4540F371CBF05A1921C04CEB16036D6D454019787F3A281721C0696859282F6D4540BD7D8B88221521C02C2884182D6D4540538687BC0E1221C0B0041355566D45406F907BE3730E21C05DDC527BBF6D4540779BBF017F0A21C0F43FB00A586E4540159DA1B3EC0921C064D20E229E6E4540EF9CB7F4F70921C0772D1629E26E4540BA8645B9D90A21C0CD2F1E74726F4540158703F6B70A21C0A65DBF00EA6F45406988112A3C0A21C05A91B4623A704540DD8BA90B010921C00CC8CB7580704540439231D3B70621C00E84816FA17045408498CFDB790421C02AA9FD04D77045406982475F340321C021A452376571454000857908480221C0E54AF6EAE2714540B07061C8CC0121C01FB2E08DE771454028F14E94A20121C0F47171DBE07145401E719464A50121C09CB5455FD07145404BF0E85BF10121C0573C6919C771454034AFD10D580221C0DECDEF28C9714540532C11B75D0321C02D78517ED4714540706AC3BF0A0421C079EA29C4DD7145409CE917B7560421C0B5711E86E671454073299630660421C08A5175DBF1714540DD537C98930321C002ECC1A43F7245400FBBEFA5660321C0E89C215E7A7245400DB9DA57200421C0AF3368F0AF724540D1B666EBEA0421C076DD8624FB72454044B5F998770521C02A3CCB0A3C734540ED7935AA310621C03F554015A47345402E7A09281B0621C0CFF9C739CB734540617DCD8BF60421C0B9C025D902744540FE7D5F46BE0421C0532CD7ED277445408C7B175C9F0521C088C76D3053744540D1C1356A630621C031C87EBF8874454045C0C817F00621C05E7B8FA5A7744540B1BEE665820721C0CF86E027B974454063BB3843B20821C075716EFED574454002B815C1E70921C096E929FBF774454086B5CDD6C80A21C0C586E0B210754540EAAE7191280D21C0A40E0FA030754540', to_timestamp(1573729200000/1000), to_timestamp(1573725600000/1000), 4, 3, NULL);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (17, '0105000020E6100000010000000102000000C400000082DF9701B2D820C0B86AFAD44AAD4540F05EC076E7D820C0E5B7E27623AD45404360CEAA6BD820C06E4E24D71DAD454071602DC95AD820C07A41132615AD45409EDF81C0A6D820C0BB600571DEAC4540186284DAC2D720C0769878E0C4AC4540F562EA1274D720C04AEAA9EFB0AC45401CDFD914B2D720C03DA1DA98A1AC4540DA5DFB6F25D820C0018747D695AC4540685BB38506D920C0AF6BBAF684AC4540C8D8C6ACF5D920C0D6EE231774AC4540ABD6C79FBADA20C07A10843763AC4540CA69A566B1DC20C04857F14338AC4540E2677AD75FDD20C02100CC6F29AC45409666E102D6DD20C0CF1543C421AC45400B6574B062DE20C08D93D8B21EAC45403FE378B008DF20C02176F12F1EAC45406AE1C280B1DF20C0A834232A1DAC454082DF97F15FE020C0A834232A1DAC4540C9DDCB80FDE020C0FFA3C87814AC4540055C45E09DE120C0B646AA2105AC454067DAA8FE32E220C07431EE1EEEAB4540C76790F894E220C0C1FCB567E4AB454045E6DDD51EE320C0D1A01BBCDCAB45408BE41165BCE320C03A69922DDAAB45408FE2717670E420C04E24AAAAD9AB4540CC60EBD510E520C05A7F9AEDCEAB45408C5F520187E520C0A471E424C2AB4540495E2E8CF7E520C040A6E144B1AB454014DF6465BCE620C0E26E69CD85AB4540085D95E778E720C0D0E5364D6FAB45400E5BF5F82CE820C04B60170D64AB45406E591347BFE820C0A8BBF5A952AB45409BD8673E0BE920C0F4EE48353EAB4540495864312AE920C0F049BEDD2EAB45409158AD5E27E920C0FF93B35F02AB454037D9F948ECE820C0E02CF8E4ECAA4540815988F6D2E820C0B1CE0670D8AA454070D95867DBE820C04F087043BAAA4540C8D8C6AC13E920C057E42457A7AA454075E287677BE920C044DF5D2C73AA454085E1ACCFCFE920C0E9F7957653AA4540A5DE3149D8EA20C00C549C4334AA4540CEBF203CCDEB20C00B58BC77FAA94540A6BD212F92EC20C03AFAC080CFA94540B3BA3149A0ED20C0BD6D339BA7A9454027C4931570EE20C02EF7D50D8FA94540A3C1D6CB56EF20C029A948A04FA9454098C01175B6EF20C064CBA39621A9454032BF8EE137F020C0CB687BDF01A94540E7BDF50CAEF020C0915D7316DFA8454081BD37D0CFF020C0C44E4C00AFA8454002B3104DECF020C0D532994E90A8454088B1185A73F120C05C2BEB906FA8454063B06944DEF120C09436C19744A845401CAD308108F320C0A6770EBC1EA845402DAB9092BCF320C0AC3072C2F3A7454008ABA6D3C7F320C0CCACD6AAC3A74540F7AB816B73F320C03C3B8EC89CA7454060A94F420EF320C03B6A56BC6EA745400CA906652AF320C04FAC2C6E35A7454028A82BCD7EF320C04648B7FBF5A6454093A70E72B1F320C0B045BA8EB7A64540FCBA7AA6EEF320C0B1CD3A2D7BA64540A9B96C726AF420C0E4EB4C2B4FA64540AEB7CC831EF520C0F49E2F652DA645409EB3A2E791F620C0E92CC850FEA545406BB0DE83B6F720C048810E30CDA545403BAE6A1781F820C09358A2B48CA54540DFADAC5A9AF920C0EF71BF444EA545403DAAB55AE6FA20C0647FD4AD34A54540ACA5E3C286FC20C04889FBC538A5454055A385975CFD20C04889FBC538A5454085BAD26AF6FE20C07EEEF3281EA5454090B6928D5E0021C009239EF815A54540B1B31707670121C0F85788EC13A54540BEB02721750221C0EF4C6C55FAA44540BCAE12D32E0321C0DC1C012DC8A44540E7B2268E4D0421C0DA78611098A445401AB1E5BDF00421C0E661B7554DA44540F1AEE6B0B50521C0552D42321CA4454040ACCA48AD0621C0F0ED91AC05A445409EA8D348F90721C0D3B1559A02A4454086A4344D720921C0F116A25021A4454080A1CF07860A21C0B065BD003FA4454063C786DD0C0C21C054DF3DD637A44540F8C29E04A20D21C0748DB369FAA34540BFC17A8F120E21C0BAF2D3FC66A3454094BD6634910F21C0C494E82C19A34540F5B5BAF74A1221C09A43D01D96A24540C7B056A5231421C08B229AE937A24540A1AAA25B561621C0A47D830C13A2454035A6BA82EB1721C0A06840B4D9A1454053A12AAEAD1921C0E31E6D6ACDA14540C2968E8B751D21C0F89E95F764A24540CD924EAEDD1E21C0278A1D1069A24540238FE24E2F2021C0216F709554A24540368A527AF12121C0B6B3377279A24540CA856AA1862321C0FFFCB15975A24540678032D1752521C0790B49D489A24540FB7B4AF80A2721C0BDD1CFB0AEA245404F778EA1B62821C0DFD04DC9B2A245402E743F9D5C2A21C0AE978AE08BA245401E701501D02B21C0462184C65CA245400E6CEB64432D21C0F3378C3BF0A14540A267038CD82E21C007E3A6FBBAA14540BD6123C0F43021C05C3611AF83A14540187E4945343421C097D295CA08A14540BD77C17D7D3621C02FA97A95D5A045400D72B52F833821C013973BE8B8A04540286CD5639F3A21C021ED9191AAA045400266211AD23C21C040225D70CFA045407C614F82723E21C0E9B89E42F2A04540D35DE322C43F21C0E9B89E42F2A045409755A52BB64221C0C97F7E32C5A04540724FF1E1E84421C00DE5B6B6B0A045404C493D981B4721C0F47940AE5AA045401E44D945F44821C05ED82A5F23A04540896429E75D4B21C02CF5E686D59F4540C560D3C8BA4C21C07A86612B489F45403B5BB139B54E21C0F9AFD6E6129F45406A560B246C5021C0016447251D9F4540E251398C0C5221C06BD7DBCD0E9F4540B54CD539E55321C00C30AC7C2B9F4540B048AB9D585521C0A628DB374A9F4540A743310A265721C0DD10B282569F45406F80A7F0995921C02416DCAD879F45404C7B439E725B21C0777CDF369E9F45400376F58C565D21C0A5806B2A9C9F45403970FF7F675F21C01FF3F094839F4540D56AC7AF566121C00F44A39B5A9F4540C5669D13CA6221C0016447251D9F45409263D9AFEE6321C0A9AFEC88D99E4540206191C5CF6421C0693A8F23759E4540E95F6D50406521C018546D58009E4540D35DE3A2FF6521C0922744F09B9D4540F36B01A6236721C09E42251E509D4540A267038CAD6821C0E3D3C518FC9C45406966DF161E6921C0F089BF51B29C454060DBF875766921C058D0BC499C9C454055DA331FD66921C0C06C2004919C4540A6D92C05146A21C04D1EC523969C4540825E8D7A466A21C0533CFC5A8A9C454019DDC416C56A21C0E4A1740A929C45406F53495C3B6E21C04C2789D6C79C4540DD6F41C39E6F21C056FF14A7E99C4540E367D7497A7221C0ABA4CB2ACF9D4540CCB806BA057321C0BC2290C5659E4540F7B6508AAE7321C0193A6AC68F9E4540E7B226EE217521C0E530EF37F69E4540FCB4B0DBA87521C00621EBE5909F454030B36F0B4C7621C0E74F433CC99F4540ECBB555E057721C099950AFDE89F4540D4B7B6627E7821C04572A09E03A04540FDA9E17C707D21C0E0C2993E48A0454025A7DB55737E21C007A5A7BA5CA0454012A19C6BA08021C07C9FDADBB6A045402AB50FFAC68221C0169D4136F0A045403BAE6AD7428521C0E0F08DC186A1454073BA5DEB668621C063E43F4F9EA1454053B70EE7858721C068A9B7D6B4A1454035B6D430EB8721C03F6F0983D1A14540B1B317E7D18821C0216F709554A2454060BAE8CB748921C0262FB2489DA245404BB85E1E348A21C0C987BB43C7A2454098B542B62B8B21C076C70AEFE3A2454079B2F3B14A8C21C00B2D1D5DF6A2454084A8E949DA8F21C0B2AB2CAD2EA3454081A6D4FB939021C079167C3345A3454017B59A7A1A9121C0C8AB84276EA34540A6B25290FB9121C089C46776D1A3454089B118DA609221C0D1977D0EEBA34540F7AB81EB609421C0B882051943A44540F1DAC509F59621C0BC5DEFC9A1A44540F7D760C4089821C0EC9BAF08C2A445401655A06D0E9921C08B2EEE99DAA4454065B27E32449A21C0FED08361FCA44540F7B6500A7F9B21C041A6F32374A54540ECB58BB3DE9B21C0C38B94A88AA54540BEB02761B79D21C02834C4D5BDA54540A1AFEDAA1C9E21C07E575996DEA5454081B1A33A039F21C0B0FC48CD6AA6454048B1441C149F21C0B045BA8EB7A64540A4B03D02F89E21C09EFD2772DEA6454079B2F3D1629F21C075CCE8CC19A745407BA9396198A221C0E35464C8F4A7454009B2C015D3A321C056F9F7DE50A8454091B0C8225AA421C0E9AF90AE74A84540ECB0865F38A421C0E0D19D9B9DA84540E4B1D656DEA321C08BD6F064C0A84540815988466DA321C036FEEA2ECDA84540B3DB4183A5A221C0B91D0070D8A84540F7DD2A4FD5A121C0D1D73CA5E1A84540385F09F461A121C090EC8CD4E9A84540E85F100E24A121C0AE13AE4BE8A845408DE05CF8E8A021C0445A6A21F7A84540', to_timestamp(1574074800000/1000), to_timestamp(1574071200000/1000), 4, 4, 6);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (18, '0101000020E6100000DACBB6D3D6A021C04A6249B9FBA84540', to_timestamp(1574076600000/1000), to_timestamp(1574074800000/1000), 2, 4, 6);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (19, '0105000020E61000000100000001020000004200000018C56ECDCFA021C00611C0CDFEA845403AC408951EA121C0B822C445E7A845401DC3CEDE83A121C0D0FB9751E9A845404FC18D0E27A221C012D49E0ADDA84540E7BDF52C62A321C0B935CBB7CEA845409BBC5C58D8A321C0FE4DC770C2A8454050BBC32354A421C00B55F8308BA8454090BB97A13DA421C0F6C529265DA8454065BD4DD194A321C06BBFF28541A845401AC1B93043A221C0EC6ACE27D9A74540396A35752B9F21C0BA5C526708A74540D56AC72FF39E21C01A956F0CCDA64540546A1F34209F21C0A9B344F170A64540286CD563779E21C0253E07E116A64540646E49D0AC9D21C00DCA8893B2A5454063AB6450BD9B21C064967F9687A54540EAAE71917C9A21C0754F19B009A54540FFB0FB3EBD9921C0FAD99EEEE8A44540E4B6DB0AA19721C0E1F616C6B6A445403DBB4E84119621C02EC71C9182A4454012B73AC9229221C0DA5C95CBDFA3454083B982B3419121C07459439580A34540E2B4C61C6C9021C0EE288CFC3BA345400FBBEFC5338E21C0FF51EBF50FA34540FCBF7F9A718C21C00587AE7BFBA24540CFC5EA065B8A21C0875B7368CDA24540A6C8F02D588921C0A9684E0592A24540CCBED084778821C03531F07424A245400FC0F4F9068821C052A819AED8A14540E4C1AA295E8721C07A322699AAA14540BBC575A7FB8521C002CD733099A14540E4C774B4368521C0B772B7C787A145405173D9A47B8321C042B85D0813A1454056770341088221C09E8FD9A1D7A04540C27BEB19738021C021ED9191AAA045405C7F6DBA2C7F21C0DA873D6879A045406982475F137E21C076913A7050A04540CA9039A0EE7821C0FEBFB0BD08A04540139687B10A7721C0C7C1B00FEC9F454005997797FC7521C0CF8A44CCB69F45407F7E07426B7521C09150C069529F45401B7F99FC327521C0E530EF37F69E45400E8289E2247421C01B61A043A49E45405C853705F57221C0D05F5DD8689E4540F885C9BFBC7221C05B75BD32FA9D454094865B7A847221C0E240A511CB9D4540E87BD558F86F21C05C0D83FFF79C4540187E49C52D6F21C07B015935D79C45407187C172D66B21C070B030F9A39C4540818BEB0E636A21C0CDAA50618B9C4540385F0984FA6921C05397D6B1839C4540F2DFCACDB96921C044CD0222819C4540CD60EB35686921C0C3EC5CB66F9C45401862840AF26821C0DF28D6BA5B9C454047E228F9E36821C0DF11500B549C4540E3E2BAB3AB6821C05F174DF8509C4540DD5E4BD7CA6721C0A2033684299C4540535FF3D29D6721C0EA389BC11E9C4540DEDF55FE6D6721C0D5930E251A9C4540496333B0356621C01E7E9AD4E39B4540DEDF55AE926521C06A212B99C59B454071602D395D6521C0D4C79A59BB9B45406960B8D9626521C0EF41ECA9B39B4540FCE08F642D6521C06AB86220B29B454074617D30036521C05750D139B69B454062E212F8AB6421C0131BD996B09B4540', to_timestamp(1574079300000/1000), to_timestamp(1574076600000/1000), 4, 4, NULL);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (20, '0101000020E6100000FD4AE7C3B36421C0F31E679AB09B4540', to_timestamp(1574081100000/1000), to_timestamp(1574079300000/1000), 1, 4, NULL);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (21, '0105000020E6100000010000000102000000800000004EAFBB9CBF6421C0A87F27E5B29B454035EE5E7E236521C01EFD95FEB69B4540CC2D097A496521C0A27EA811BA9B454005ECEA11EE6521C0064A234DD89B4540E8EAB05B536621C0764B5CF4E89B45405660438A656721C025115E18189C45402860E46B766721C0D83DCF57229C45408C5F52B1AE6721C0C1D602EE259C4540E35CABA8A06821C05F174DF8509C4540B55C4C8AB16821C0B425AB375B9C454063DC487DD06821C01D6E5B7D669C4540465C19EED86821C0E29306E9779C45403B714FF7286921C0FC7189ECA19C4540EA721B688B6821C0510D0832009D45400075A515CC6721C0D45D0AA03B9D4540B57811757A6621C096B5311A799D454008B675B2606521C0ABA4CB2ACF9D454025B7AF68FB6421C0EFA1EFFC449E454070B8483D856421C08F0607059A9E4540035A3082A96321C049241157E69E454090DCA7FBBF6221C069FBD0FF169F45406E5E185B146221C059761EA2319F4540515D3BE7976021C00620EBF2689F454062616583245F21C02416DCAD879F4540026621DA785D21C0A5806B2A9C9F45400E6CEB64515B21C08961F71D9A9F45403870FFBFD25921C010ACC6C68B9F45408A74FDD9485821C03AD8D90B6D9F45401962E17C4C5521C08BA754444C9F45400266215AE45321C059761EA2319F4540B8698DB9925221C06BD7DBCD0E9F4540826F83C6815021C053E753F3149F4540E0765B85DE4D21C01B8FBC4A239F4540897AC7E48C4C21C05D423976549F4540297CA996FA4B21C04A0AC74FA29F45409A7EF180194B21C07BA485D1E19F45400E828962DE4921C0F0A462EF10A0454083B3B868DE4721C056E2E7CE35A0454040B79927874621C0C7020F246EA0454004BA2AEF894521C0CC4444479EA04540B9BD964E384421C052C8CCFABBA04540B7C14B8BCA4221C086730E45C8A04540FCC549A5404121C099F508A8D8A04540CCC99FC3E33F21C0F2011330EFA0454025CD4DE6B33E21C0E9B89E42F2A04540C9D25934AE3C21C0B4AEFD63CDA045408BB832BCF23A21C02CD7C297ABA0454056BE28C9E13821C0202786B0AFA0454060CABCDE923421C0D9326E80FCA0454098D1AADEFA3121C0D9224BD75FA1454056B3592AEB2F21C08C2417499DA145403DBB4E44152D21C09D410679FAA14540D7BF953B6F2B21C028120A3B70A24540C76DB795702921C08C6A9273A4A24540ED736BDF3D2721C0BDD1CFB0AEA24540E87BD558622421C0295A7A7E7BA245407281F7E7672221C0B6EC6E4D73A24540F885C97FC72021C09690B5A156A24540398C6788891E21C0278A1D1069A24540108F6DAF861D21C033A6D90367A245400E982720511A21C058BC0EFEE5A14540009B1706431921C0CCC11E5ECBA14540A4A023543D1721C03B3128D9DFA14540B28795B0D01321C0E631E1F539A24540518F41ED161121C08776021FC1A245407695F536E40E21C0AC7656F039A345406998E51CD60D21C037DE014673A3454026758F14720D21C07CD9E99AD7A345401D76DF0B180D21C02E5D93FB12A44540D3794B6BC60B21C054DF3DD637A44540BC7D8B485E0A21C052F386EE3BA445404683ADD7630821C0BAE27DA604A44540F287692EB80621C034E60482FEA34540388C67482E0521C0FCC384992DA44540598EF1F56E0421C09A2BAD667BA44540AC8FFF29F30321C0707E3165A6A44540FD93FD43690221C011ED2431F4A445406BB0DEC3990121C001585CD40FA5454063B633EF77FF20C009239EF815A545402DBC29FC66FD20C0FA15E8B936A54540B9C360D9B2FA20C0E0A6CAA733A545400AC85EF328F920C00C65178158A545405DC96C27ADF820C0A02E46667FA54540E4B6DBCA72F720C0D5104072D8A5454022BB6485EEF520C0D27100CF13A645403ABF038175F420C0A09B46E342A645407A5A3500C9F320C0AE752D6985A645408E5CBFAD09F320C093C40A455AA74540745CD5EE14F320C0DF1B803F85A745407A5A3500C9F320C081EC0110D5A74540F35B2DF341F320C0E1A0A58014A84540A75F9952F0F120C0BD4F346E3DA84540FA60A78674F120C09829FD8A6EA84540DA625DB6CBF020C089C9EB8399A84540DA625DB6CBF020C0484D4BDBD4A84540B25B593523F020C05A621FF705A94540ED5DCDA158EF20C03D160F363DA94540C25F83D1AFEE20C0A7CA1FC782A94540A267038CDFEB20C095AB1237EFA94540796A09B3DCEA20C046B852A32EAA4540A16DCD16B8E920C052A26CD463AA454084B3B828E7E820C0E959CEB4B7AA4540F1B3EBC4BFE820C0182AB97BDAAA454093B2DD903BE920C0E24B170920AB454056B3590AF8E820C02C3A60D543AB45407EB5581733E820C0D9BBDA725FAB454030B8747F3BE720C06DF520D06FAB4540CC5A21CB8CE520C010C5FBA1C1AB454024DE891D5AE420C068583899D6AB4540ABE1965E19E320C07CEC6233DBAB4540C1E3200C5AE220C0FF495673E6AB45403C66232676E120C016E02FB007AC4540D9B011C08DE020C0DFA0029018AC454030B36FEBB7DF20C0606A9F9B1AAC4540DCB72B420CDE20C09A103CA71CAC454007BC3F9D8DDC20C0FFAEBE4939AC45406DC2C76444DA20C091EB4A5A69AC454033CA5D607FD720C06E1CB3819DAC454099CBE0F3FDD620C0B92E6EA4A3AC4540ACCB5553F8D620C04AEAA9EFB0AC454068CA31DE68D720C053A40501B4AC4540E7C8C48BF5D720C0D57E4780CAAC4540C7628BF492D820C0BB600571DEAC4540E4E2BA838AD820C075010B3FECAC45406DE3D7DE57D820C0475A871A13AD45400064AF6922D820C0AD880DC019AD45409AE336FD46D820C05B42926520AD454030E4535814D820C0D0D5311E41AD4540', to_timestamp(1574083800000/1000), to_timestamp(1574081100000/1000), 4, 4, NULL);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (22, '0105000020E610000001000000010200000089000000ECE734D719D820C094A3C68F3EAD454084666C7398D820C02D544005E2AC4540696682B4A3D820C0580FE659DAAC45401068D9C50BD820C0C3174D1ACFAC4540E4E884CEBFD720C01D353A4CC1AC4540C1E9EA0671D720C0246F3CFBB2AC4540F4DB155174D720C06A11B038A7AC4540B25A37ACE7D720C0BE1498049EAC4540CA580C1D96D820C0B4C4F4A78DAC454057D67E6274D920C0041D63C87CAC45404153EA8D90DA20C0B6A5E74866AC45402AD055B9ACDB20C0503775464FAC4540205665A45DDD20C0C1D799752AAC45409BB757241DDE20C08D93D8B21EAC454002B3102DC3DF20C07F560AAD1DAC4540C6AFD769EDE020C013B4658416AC454080AC9EA617E220C0CDBD53D0F6AB45408EA9AEC025E320C03A69922DDAAB454091A5F98393E420C04A48F121D8AB4540267B59DF8AE520C010C5FBA1C1AB4540F578E57255E620C0F36384AD96AB4540FB76458409E720C0FD2271E773AB45400874559E17E820C0F61D2D8A63AB4540717123F503E920C07E49DFC340AB4540D570913A3CE920C0C1DE0DE619AB4540B271F772EDE820C0B6B20A08F3AA454034729F6EC0E820C06E64BB06C6AA4540D570913A3CE920C0EAB4BBED94AA45405B6F9947C3E920C0E9F7957653AA45404F6CBFA2DCEA20C0C32CCEBA32AA45409C69A33AD4EB20C040049D7DFBA94540E86537DB25ED20C02BB666F9B7A94540318123EAB9EE20C0492AE9D284A94540367F83FB6DEF20C0D97A15AC51A94540FE7D5F86DEEF20C063F4D84914A94540437C93157CF020C01F443F69EDA845404C7B431ED6F020C026A29D7CC4A84540667B2DDDCAF020C00E4D4FB3A1A84540497AF32630F120C061AE47C678A84540CF78FB33B7F120C00E4B800858A84540F27795FB05F220C0EB52755639A84540A77537D0DBF220C062584BA41AA845400874551E6EF320C02A77732D06A845402B73EFE5BCF320C0863D4B63E3A745406C73C363A6F320C08B2FFB98C0A74540A56E1DCE2AF320C081C442638BA745402DBC29DCEBF220C0779FE0A93FA74540FCBA7AC656F320C0F67440790BA7454046BAFE4C9AF320C0ED214F3CD5A6454018BA9F2EABF320C09EB101F39CA645402C73EFE516F420C0A1274BB566A6454071712375B4F420C0E99D3EAD39A64540E56EF1CBA0F520C0E9A91B1D21A645408F6C93A076F620C06230CE5C00A64540B7698D7979F720C0100E2A54D3A545404667458F5AF820C02974050F9CA545400AB88AA0EBF820C09C05ADED6AA5454091B692AD72F920C0864FD15050A5454004B460045FFA20C0B53505CC39A5454081B1A3BA45FB20C064E8AC9530A5454099AF782BF4FB20C088C5C0A132A545405DAD04BFBEFC20C0194FDEB335A5454075ABD92F6DFD20C0FA15E8B936A54540A1A9230016FE20C043C4847D2CA54540A7A78311CAFE20C0EF53E9221DA5454035A53B27ABFF20C0F85788EC13A54540DCA18D04DB0021C0F85788EC13A54540989FA438AB0121C01B2346C80DA54540EFACD142480221C0C9319B6DFEA445405BABEF90DA0221C0A80D2EB8DFA445400FAA56BC500321C0ECBD6AEABCA4454001A8416E0A0421C0BFF9E746A1A44540CAA61DF97A0421C01D5B3C9D84A44540B1553238E20421C0E661B7554DA44540D454CCFF300521C0167AFD4034A44540F053F167850521C0A436C85C23A4454075D2B3A4090621C0FE91897812A4454021D1A570850621C03BEEAF3507A445407BCF4E5F1D0721C0A55D2D8E00A445408EBA47CAE70721C0268C419401A4454012B73A89280921C0AED3F41918A44540A9B467FE030A21C06D2019CA35A4454025B2AAB4EA0A21C0B882051943A445402BAF456FFE0B21C0F77A62E239A445400AADBBC1BD0C21C01333EE6825A4454090ABC3CE440D21C0BAE27DA604A44540BFAA5D96930D21C04F0B80C5DEA3454098AA73D79E0D21C01B1D138FAAA34540FDA9E11CD70D21C0954A74A182A3454045AF2FEE5D0E21C05E9253A157A345406EAC29C7600F21C0D9EB357625A345406BAA14791A1021C00B2D1D5DF6A2454001A841EEF51021C0C987BB43C7A24540E1A4F2E9141221C09A43D01D96A2454038B21FD4491321C01714D8A757A2454073AF8E0C471421C0D2E1E7CA32A2454048AB7AB1C51521C0F2BFF11E16A2454055A88ACBD31621C0D9D82354F4A1454091A5F903D11721C0430DCCA1D6A145401FA3B119B21821C0CCC11E5ECBA145401D11F7D1B71A21C00C1AD847F2A14540710C3B7B631C21C0468CB51A40A245408708FB9DCB1D21C0278A1D1069A24540A1021BD2E71F21C0AF260DDF60A245407CFC66881A2221C035E8A4286DA2454010F87EAFAF2321C0FFFCB15975A245402BF29EE3CB2521C09A43D01D96A24540FDEC3A91A42721C0DFD04DC9B2A24540CFE7D63E7D2921C0262CD27FA6A24540EDE2466A3F2B21C02AB92B4171A24540BADF8206642C21C0009A0BD133A2454087DCBEA2882D21C058BC0EFEE5A14540E2D6B2548E2F21C09949915BA0A145403ED1A606943121C0D7C3C9336FA14540AEEF56F9353421C0E2D002FC10A14540D2E82636AC3621C09E8FD9A1D7A04540B4E122F5383921C07C9FDADBB6A04540CDDAF231AF3B21C07C9FDADBB6A04540AFD3EEF03B3E21C09FFB861DECA045400ACEE2A2414021C0169D4136F0A045401DC952CE034221C040225D70CFA0454041C2220B7A4421C0646B9CF4BAA0454099BBC6C5D94621C072BFA73671A04540F5B5BA77DF4821C05ED82A5F23A045400DAF8AB4554B21C07B024203EA9F4540E5ABC6507A4C21C06C3B2C4A779F4540EAA926622E4D21C059761EA2319F454005A446964A4F21C038EC4D0C199F4540E49BF25D315221C0545559DA109F454087941A9FD45421C007C1E71E469F4540F08B1E6BE85721C01D0384CD629F45403EDD3A9C4D5921C0DD10B282569F4540D4D9A2BA885A21C0227F6FAEDF9E454076B6A8EE0C5B21C0C3493682AE9E454032B4BF22DD5B21C0B35545B4B69E4540C1B23C8F5E5C21C07CA093ECBF9E4540B1B317270A5C21C0EA363025F39E4540', to_timestamp(1574152200000/1000), to_timestamp(1574148600000/1000), 4, 2, 19);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (23, '0101000020E61000001348895DDB5B21C05053CBD6FA9E4540', to_timestamp(1574154000000/1000), to_timestamp(1574152200000/1000), 2, 2, 19);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (24, '0105000020E6100000010000000102000000240000003CDC5EAEF85B21C0CE2A89F9009F454060BAE8AB585C21C0EB0AD5F2C09E454080BD37B0395B21C048163E9BB29E45405DBE9DE8EA5A21C063092550A69E4540F1BEBA43B85A21C05DF37FA1B39E45401FBF1962A75A21C0A857A237CC9E454001C3E4DF445921C025EA93B45E9F45405FE0FD79ED5821C01BC92B9B849F454045E1D811995821C0C8530869919F454062E212C8335821C0C8530869919F454023634971ED5721C07DB9BE2A879F454049633330E25721C06C4AC0D9799F4540C7628B340F5821C0AA45AAA1709F4540E261B09C635821C0CFBEE5A7719F4540A8E04657D15821C06AE0DD5C7A9F45406EDD0D94FB5921C07E3B01CD8C9F4540435A04601D5B21C0261F8311989F4540B6D78CE6065C21C02C7C4EA79B9F4540BA54E2D0175D21C0A06C14A19A9F4540645D53B4A95E21C0190259568E9F45405DD9E3471A6021C036980EC7769F45407ED668C1226121C0BEA81BA85C9F4540F6D25B80636221C063DF08062D9F454039593F09C06321C02ED8AFB4E09E454007D785CC876421C06BBF3EF2969E4540085890C3E16421C077BAF49F5F9E45407BAE3EF5606521C097A6A895E09D4540E5AB0C4C4D6621C01D45785F849D4540FA60A706056821C05E211E3B2B9D4540ED5DCD611E6921C024C136D0C69C4540F65C7D6A786921C077169BA0959C45407F577318146A21C04D1EC523969C454006D7854C3E6A21C0CB01D2D7899C45402AD61F148D6A21C07310A5678C9C45407E526EE4DB6B21C070B030F9A39C454045D03F48A96C21C001ABEFC1AF9C4540', to_timestamp(1574155800000/1000), to_timestamp(1574154000000/1000), 4, 2, 19);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (25, '0105000020E6100000010000000102000000100000009C952511A80C21C04CAA29722A754540129BD240B30A21C0BA7743940C754540329E2145940921C0FF5140B6EE74454094A144C75E0821C07A24D1B1CB74454076A50F45FC0621C0D5083AC4AB7445409CA6BE5A910621C0F8D5DD1394744540E7A7572F1B0621C09E1A0D2674744540105D0AE7870521C0081C857E4C744540C1DE1B28ED0421C05EAEFF692774454001DFEFA5D60421C0BEF14CD0127445402D5E449D220521C021291A10F973454022BB6485ED0521C0643433ADDA73454045BAFE4C3C0621C05172F369AF73454022BB6485ED0521C04E1BFFC97773454089BCE7186C0521C0CF4413CD33734540EEBD6AACEA0421C0199C1EC0ED724540', to_timestamp(1574155800000/1000), to_timestamp(1574154300000/1000), 4, 3, 21);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (26, '0105000020E61000000100000001020000000A00000086A4346D9FDA20C00001140C4FAE4540ABA5E38234DA20C09502C5675FAE454028A82B6D53D920C0E6DC55DA73AE45401AAB1B5345D820C09B811F4787AE4540C3AE87B2F3D620C0F04CCBF2A5AE454022B095E677D620C0D4992C65BAAE45408355D37958D620C05F4F35B5C8AE45404DD5B92B6CD620C0E3683771D3AE4540FB54B61E8BD620C05E990DD8E5AE4540CBD4113099D620C013EA26A5F3AE4540', to_timestamp(1574149800000/1000), to_timestamp(1574148600000/1000), 4, 5, 23);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (27, '0105000020E6100000010000000102000000040000009FD5480893D620C013EA26A5F3AE4540DC953FEE7DD620C096809F7BE6AE4540413801C5A1D520C069A64E73F0AE454037B84695A4D520C026A6035EF2AE4540', to_timestamp(1574150400000/1000), to_timestamp(1574149800000/1000), 3, 5, 23);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (28, '0105000020E6100000010000000102000000080000005E5424A4F0D220C0D1FAC415C3AE45402855157DA7D220C0F4880D10C2AE45407456AE5131D220C08BA31288D7AE45404059B4782ED120C0B44C34C1E1AE45405DD9E30726D120C0265CC98DD8AE4540C7ED2B8A47D020C050BA28DBF1AE45403817086E07D020C08DD35974DFAE454072F73270F4CF20C0B44C34C1E1AE4540', to_timestamp(1574153100000/1000), to_timestamp(1574151600000/1000), 3, 1, 25);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (29, '0101000020E6100000A8FFACF9F1CF20C0E0D91EBDE1AE4540', to_timestamp(1574156700000/1000), to_timestamp(1574153100000/1000), 2, 1, 25);
INSERT INTO public.te_activity (id, geom, ts_end, ts_init, category_id, employee_id, event_id) VALUES (30, '0101000020E6100000F6D1A92B9FD520C0C632FD12F1AE4540', to_timestamp(1574154000000/1000), to_timestamp(1574150400000/1000), 2, 5, 23);

--
-- Name: te_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.te_activity_id_seq', 30, true);

-- los datos están preparados para mostrar el 19 de noviembre, por lo que hay que avanzarlos al día actual

update te_planned_event
set date = date + interval '1' day * (extract (day from now() - to_date('20191119', 'YYYYMMDD')));
update te_activity
set ts_init = ts_init + extract (day from now() - to_date('20191119', 'YYYYMMDD')) * INTERVAL '1 day',
    ts_end = ts_end + extract (day from now() - to_date('20191119', 'YYYYMMDD')) * INTERVAL '1 day';
update te_planned_event
set state = 1
where date = NOW() AND end_time < CURRENT_TIME;

/*% } %*/
/*% if (feature.MWM_TrajectoryAnnotation) { %*/
--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.ta_tag (id, name) VALUES (1, 'INACTIVO');
INSERT INTO public.ta_tag (id, name) VALUES (2, 'ACTIVO');
INSERT INTO public.ta_tag (id, name) VALUES (3, 'ANDANDO');
INSERT INTO public.ta_tag (id, name) VALUES (4, 'CONDUCIENDO');
/*% } %*/
/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
--
-- Data for Name: ta_taxonomy_entity; Type: TABLE DATA; Schema: public; Owner: postgres
--

insert into public.ta_taxonomy_entity (id,name,json_tree)
values (1,'A','{"id":1,"node_type":"Decision","children":[{"id":1,"child":{"id":2,"node_type":"Tag","children":[],"tag":"ANDANDO"},"predicate":{"id":1,"predicate_type":"Has Tag","tag":"ANDANDO"}},{"id":2,"child":{"id":3,"node_type":"Tag","children":[],"tag":"CONDUCIENDO"},"predicate":{"id":2,"predicate_type":"Constant","value":true}}]}');

--
-- Data for Name: ta_taxonomy_entity_employees; Type: TABLE DATA; Schema: public; Owner: postgres
--

insert into public.ta_taxonomy_entity_employees (taxonomy_entity_id, employees_id)
select 1, id from te_employee;
/*% } %*/
-- END TRAJECTORY EXPLOITATION DATA --
/*% if (feature.T_P_RedSys) { %*/

-- SEQUENCE FOR REFERENCE GENERATION
CREATE SEQUENCE IF NOT EXISTS p_payment_reference_seq
  INCREMENT 1
  MINVALUE 1000
  MAXVALUE 999999999999
  START WITH 1000;

-- START SEQUENCE AT RANDOM POSITION FOR DEVELOPMENT
SELECT setval('p_payment_reference_seq', floor(random() * (999999999999-1000+1) + 1000)::bigint, false);
/*% } %*/

/*% if (feature.DM_DS_GTFS) { %*/

-- TABLE WITH SHAPES LINESTRINGS
CREATE TABLE shapes_aggregated AS
SELECT shape_id, st_makeline(shape_pt_loc ORDER BY shape_pt_sequence) AS geom FROM t_shapes ts GROUP BY shape_id;

/*% } %*/
