# Visual Secret Sharing

```bash
docker build -t vss-oracle-db .
```
```bash
docker run -d \
  --name oracle-db-vss \
  -p 1521:1521 \
  -p 5500:5500 \
  --restart unless-stopped \
  vss-oracle-db
```

```bash
ocker exec -it oracle-db-vss bash -c "source /home/oracle/.bashrc; sqlplus / as sysdba"
```

```bash
docker exec -it oracle-db-vss bash -c "source /home/oracle/.bashrc; sqlplus system/Parola1\!@XEPDB1"
```