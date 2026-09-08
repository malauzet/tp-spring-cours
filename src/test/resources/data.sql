INSERT INTO region (id, code, nom) VALUES
                                       (1, '84', 'Auvergne-Rhône-Alpes'),
                                       (2, '11', 'Île-de-France');

INSERT INTO departement (id, code, nom, id_region) VALUES
                                                       (1, '69', 'Rhône', 1),
                                                       (2, '01', 'Ain', 1),
                                                       (3, '75', 'Paris', 2),
                                                       (4, '2A', 'Corse-du-Sud', NULL);

-- IDENTITY columns don't advance when ids are supplied explicitly;
-- restart them above the fixture so save() doesn't collide with id 1..4
ALTER TABLE region ALTER COLUMN id RESTART WITH 100;
ALTER TABLE departement ALTER COLUMN id RESTART WITH 100;
ALTER TABLE ville ALTER COLUMN id RESTART WITH 100;