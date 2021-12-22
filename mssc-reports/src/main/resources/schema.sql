create table reports (
    id int AUTO_INCREMENT,
    report_name varchar(32) NOT NULL,
    report_file varchar(64) NOT NULL,
    report_description varchar(255) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    PRIMARY KEY (id)
);

insert into reports(report_name, report_file, report_description, createdBy)
values('Sample Report', 'onecard.jrxml','Onecard Sample Report', 'Adebola Omoboya');
