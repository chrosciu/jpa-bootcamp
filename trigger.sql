DROP TABLE employee_audit;

CREATE TABLE employee_audit
(
    id bigint not null primary key,
    firstname varchar(255),
    lastname  varchar(255),
    timestamp varchar(255)
);

CREATE OR REPLACE FUNCTION insert_employee_audit() RETURNS TRIGGER AS $$
BEGIN
INSERT INTO employee_audit(id, firstname, lastname, timestamp) VALUES (new.id, new.firstname, new.lastname, current_timestamp);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER insert_employee_trigger AFTER INSERT ON employee FOR EACH ROW EXECUTE PROCEDURE insert_employee_audit();

SELECT * FROM pg_trigger;

SELECT * FROM pg_trigger, pg_class WHERE relname = 'employee';

INSERT INTO employee(id, firstname, lastname) VALUES (nextval('hibernate_sequence'), 'Jan', 'Kowalski');

DROP TRIGGER insert_employee_trigger ON employee;