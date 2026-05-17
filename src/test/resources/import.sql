CREATE OR REPLACE VIEW view_saldo AS
SELECT
    c.id,
    c.numero,
    c.tipo,
    COALESCE(SUM(
                     CASE
                         WHEN t.tipo = 'DEPOSITO'      AND t.conta_destino_id = c.id THEN  t.valor
                         WHEN t.tipo = 'SAQUE'         AND t.conta_origem_id  = c.id THEN  t.valor   -- já negativo
                         WHEN t.tipo = 'TRANSFERENCIA' AND t.conta_destino_id = c.id THEN  t.valor   -- crédito
                         WHEN t.tipo = 'TRANSFERENCIA' AND t.conta_origem_id  = c.id THEN -t.valor   -- débito
                         ELSE 0
                         END
             ), 0) AS saldo
FROM conta c
         LEFT JOIN transacao t
                   ON c.id = t.conta_origem_id
                       OR c.id = t.conta_destino_id
GROUP BY c.id, c.numero, c.tipo
ORDER BY c.id;