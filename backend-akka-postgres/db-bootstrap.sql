CREATE TABLE "foos" ("id" SERIAL NOT NULL PRIMARY KEY,"bar" VARCHAR NOT NULL,"zut" INTEGER NOT NULL);
INSERT INTO "foos" ("bar", "zut") VALUES ('hello', 42), ('akka/postgres', 13);
