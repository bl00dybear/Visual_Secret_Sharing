FROM container-registry.oracle.com/database/express:21.3.0-xe

ENV ORACLE_PWD=Parola1!
ENV ORACLE_CHARACTERSET=AL32UTF8

EXPOSE 1521
EXPOSE 5500