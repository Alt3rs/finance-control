package com.example.finance_control.domain.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {

    //ALIMENTAÇÃO E BEBIDAS
    FOOD("Alimentação", "🍽️", "#FF6B6B", "Gastos com comida, restaurantes, delivery"),
    GROCERIES("Supermercado", "🛒", "#4ECDC4", "Compras de mercado e produtos básicos"),

    //MORADIA
    RENT("Aluguel", "🏠", "#45B7D1", "Aluguel, financiamento, IPTU"),
    HOUSE_BILLS("Contas da Casa", "💡", "#96CEB4", "Luz, água, gás, internet, telefone"),
    MAINTENANCE("Manutenção", "🔧", "#FFEAA7", "Reparos, reformas, limpeza"),

    //TRANSPORTE
    FUEL("Combustível", "⛽", "#FD79A8", "Gasolina, álcool, diesel"),
    PUBLIC_TRANSPORT("Transporte Público", "🚌", "#6C5CE7", "Ônibus, metrô, trem, táxi, uber"),
    VEHICLE_MAINTENANCE("Manutenção Veículo", "🔧", "#A29BFE", "Mecânico, revisão, seguro, IPVA"),

    //VESTUÁRIO
    CLOTHING("Roupas", "👕", "#74B9FF", "Roupas, sapatos, acessórios"),

    //SAÚDE
    HEALTH("Saúde", "🏥", "#55A3FF", "Médico, dentista, exames, remédios"),
    GYM("Academia", "💪", "#00B894", "Academia, esportes, atividades físicas"),

    //EDUCAÇÃO
    EDUCATION("Educação", "📚", "#FDCB6E", "Cursos, livros, material escolar"),

    //LAZER
    ENTERTAINMENT("Entretenimento", "🎮", "#E17055", "Cinema, jogos, streaming, hobbies"),
    TRAVEL("Viagem", "✈️", "#00CEC9", "Viagens, hotéis, turismo"),

    //FINANCEIRO
    INVESTMENT("Investimento", "📈", "#2D3436", "Aplicações, ações, fundos"),
    LOAN("Empréstimo", "💳", "#636E72", "Empréstimos, financiamentos, cartão"),

    //OUTROS
    GIFTS("Presentes", "🎁", "#E84393", "Presentes, doações, caridade"),
    OTHERS("Outros", "📋", "#B2BEC3", "Gastos diversos não categorizados"),

    //RECEITAS
    SALARY("Salário", "💰", "#00B894", "Salário, bonificações"),
    FREELANCE("Freelance", "💻", "#0984E3", "Trabalhos extras, consultoria"),
    INVESTMENT_RETURN("Retorno Investimento", "📊", "#6C5CE7", "Dividendos, juros, lucros"),
    VENDA("Venda", "💸", "#FDCB6E", "Venda de produtos, serviços"),
    OTHER_INCOME("Outros Ganhos", "💎", "#A29BFE", "Outros tipos de receita");

    private final String displayName;
    private final String emoji;
    private final String color;
    private final String description;

    Category(String displayName, String emoji, String color, String description) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.color = color;
        this.description = description;
    }


    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getColor() { return color; }
    public String getDescription() { return description; }

    // Para JSON serialization/deserialization
    @JsonValue
    public String getCode() {
        return this.name();
    }

    @JsonCreator
    public static Category fromCode(String code) {
        for (Category category : Category.values()) {
            if (category.name().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Categoria inválida: " + code);
    }

    // Métodos auxiliares para frontend
    public static Category[] getExpenseCategories() {
        return new Category[] {
                FOOD, GROCERIES, RENT, HOUSE_BILLS, MAINTENANCE,
                FUEL, PUBLIC_TRANSPORT, VEHICLE_MAINTENANCE, CLOTHING,
                HEALTH, GYM, EDUCATION, ENTERTAINMENT, TRAVEL,
                LOAN,INVESTMENT, GIFTS, OTHERS
        };
    }

    public static Category[] getRevenueCategories() {
        return new Category[] {
                SALARY, FREELANCE, INVESTMENT_RETURN, VENDA, OTHER_INCOME
        };
    }


    public boolean isRevenueCategory() {
        return this == SALARY || this == FREELANCE ||
                this == INVESTMENT_RETURN || this == VENDA ||
                this == OTHER_INCOME;
    }


    public boolean isExpenseCategory() {
        return !isRevenueCategory();
    }
}

