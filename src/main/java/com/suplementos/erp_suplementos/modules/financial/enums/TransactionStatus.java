package com.suplementos.erp_suplementos.modules.financial.enums;

public enum TransactionStatus {
    PENDENTE,   // Lançado na agenda, mas não pago/recebido
    PAGO,       // Dinheiro efetivamente saiu/entrou (Liquidado)
    CANCELADO   // Erro de lançamento ou conta que não será paga
}
