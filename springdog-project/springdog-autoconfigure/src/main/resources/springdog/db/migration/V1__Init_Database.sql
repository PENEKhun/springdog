create table APP.ENDPOINT
(
    HTTPMETHOD             VARCHAR(10)  not null,
    ID                     BIGINT       not null
        primary key,
    ISPATTERNPATH          BOOLEAN      not null,
    METHOD_SIGNATURE       VARCHAR(255) not null,
    PATH                   VARCHAR(255) not null,
    RULEBANTIMEINSECONDS   INTEGER      not null,
    RULEIPBASED            BOOLEAN      not null,
    RULEPERMANENTBAN       BOOLEAN      not null,
    RULEREQUESTLIMITCOUNT  INTEGER      not null,
    RULESTATUS             VARCHAR(255) not null,
    RULETIMELIMITINSECONDS INTEGER      not null
);

create table APP.ENDPOINTPARAMETER
(
    ENABLED     BOOLEAN      not null,
    ENDPOINT_ID BIGINT
        references APP.ENDPOINT,
    ID          BIGINT default GENERATED_BY_DEFAULT generated always as identity
        primary key,
    NAME        VARCHAR(255) not null,
    TYPE        VARCHAR(255) not null
);

create table APP.ENDPOINTVERSIONCONTROL
(
    DATEOFVERSION       TIMESTAMP   not null
        primary key,
    FULLHASHOFENDPOINTS VARCHAR(64) not null,
    ID                  BIGINT      not null
);

create table APP.ENDPOINTCHANGELOG
(
    CHANGETYPE              VARCHAR(255) not null,
    DETAILSTRING            VARCHAR(255),
    ID                      BIGINT       not null
        primary key,
    ISRESOLVED              BOOLEAN      not null,
    REFLECTED_VERSION       TIMESTAMP    not null
        references APP.ENDPOINTVERSIONCONTROL,
    TARGETMETHOD            VARCHAR(30)  not null,
    TARGETPATH              VARCHAR(255) not null,
    TARGET_METHOD_SIGNATURE VARCHAR(255) not null
);

create table APP.ENDPOINT_METRIC
(
    AVERAGE_RESPONSE_MS    BIGINT not null,
    ENDPOINT_ID            BIGINT not null
        references APP.ENDPOINT,
    FAILURE_WITH_RATELIMIT BIGINT not null,
    ID                     BIGINT default GENERATED_BY_DEFAULT generated always as identity
        primary key,
    METRIC_DATE            DATE   not null,
    PAGE_VIEW              BIGINT not null
);

create table APP.SYSTEM_METRIC
(
    CPU_USAGE_PERCENT    DOUBLE    not null,
    DISK_USAGE_PERCENT   DOUBLE    not null,
    ID                   BIGINT default GENERATED_BY_DEFAULT generated always as identity
        primary key,
    MEMORY_USAGE_PERCENT DOUBLE    not null,
    TIMESTAMP            TIMESTAMP not null
);

