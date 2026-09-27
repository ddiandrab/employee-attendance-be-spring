CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    "passwordHash" TEXT NOT NULL,
    role VARCHAR(32) NOT NULL,
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS department (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    "employeeNumber" TEXT NOT NULL UNIQUE,
    "firstName" TEXT NOT NULL,
    "lastName" TEXT,
    email TEXT,
    phone TEXT,
    "photoUrl" TEXT,
    position TEXT,
    "joinDate" DATE,
    "isActive" BOOLEAN NOT NULL DEFAULT TRUE,
    "userId" INTEGER NOT NULL UNIQUE REFERENCES "user"(id),
    "departmentId" INTEGER REFERENCES department(id),
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS "attendanceRecord" (
    id SERIAL PRIMARY KEY,
    "employeeId" INTEGER NOT NULL REFERENCES employee(id),
    "attendanceDate" DATE NOT NULL,
    "checkIn" TIMESTAMPTZ,
    "checkOut" TIMESTAMPTZ,
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE ("employeeId", "attendanceDate")
);

CREATE TABLE IF NOT EXISTS notification (
    id SERIAL PRIMARY KEY,
    "recipientId" INTEGER NOT NULL REFERENCES "user"(id),
    type VARCHAR(100) NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    "isRead" BOOLEAN NOT NULL DEFAULT FALSE,
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
