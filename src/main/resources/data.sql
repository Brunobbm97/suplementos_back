-- 1. Inserindo Categorias de Receita (Entradas)
INSERT INTO financial_categories (name, type) VALUES ('Venda de Produtos', 'REVENUE');
INSERT INTO financial_categories (name, type) VALUES ('Venda de Serviços/Consultoria', 'REVENUE');
INSERT INTO financial_categories (name, type) VALUES ('Ajuste de Saldo Positivo', 'REVENUE');

-- 2. Inserindo Categorias de Despesa (Saídas - Operacional)
INSERT INTO financial_categories (name, type) VALUES ('Compra de Mercadoria', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Aluguel e Condomínio', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Energia Elétrica', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Internet e Telefone', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Marketing e Social Media', 'EXPENSE');

-- 3. Inserindo Categorias de Despesa (Saídas - Pessoal/RH)
INSERT INTO financial_categories (name, type) VALUES ('Pró-labore', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Comissões', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Limpeza e Manutenção', 'EXPENSE');

-- 4. Inserindo Categorias de Impostos e Taxas
INSERT INTO financial_categories (name, type) VALUES ('Impostos (Simples Nacional/MEI)', 'EXPENSE');
INSERT INTO financial_categories (name, type) VALUES ('Taxas de Máquina de Cartão', 'EXPENSE');