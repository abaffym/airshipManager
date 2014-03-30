CREATE TABLE "AIRSHIP" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(50),
    "CAPACITY" INTEGER,
    "PRICE" DECIMAL
);

CREATE TABLE "CONTRACT"(
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "STARTDATE" DATE,
    "NAMEOFCLIENT" VARCHAR(50),
    "PAYMENTMETHOD" VARCHAR(12),
    "AIRSHIPID" BIGINT REFERENCES AIRSHIP(ID),
    "DISCOUNT" FLOAT,
    "LENGTH" INTEGER
);
