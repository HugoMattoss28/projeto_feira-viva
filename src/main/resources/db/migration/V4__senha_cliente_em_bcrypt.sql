-- V4__senha_cliente_em_bcrypt.sql
-- Migra a senha da cliente de teste para BCrypt (Aula 14)
-- Substitua o hash abaixo pelo gerado no passo anterior
UPDATE clientes
SET senha_hash = '$2a$10$x69.2phUOVAiOD.EKnoSMOll9PMyDUuIP0SXBC1Nal6Iy9p9Bqzq.'
WHERE email = 'maria@email.com';

