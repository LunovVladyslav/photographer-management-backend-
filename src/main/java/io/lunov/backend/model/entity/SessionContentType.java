package io.lunov.backend.model.entity;

import jakarta.persistence.Embeddable;


public enum SessionContentType {
    // Портретні фотосесії
    PORTRAIT,
    BUSINESS_PORTRAIT,
    FASHION,
    BEAUTY,

    // Сімейні та особисті події
    FAMILY,
    CHILDREN,
    NEWBORN,
    PREGNANCY,
    LOVE_STORY,

    // Весільні
    WEDDING,

    // Святкові та події
    BIRTHDAY,

    // Професійні
    PRODUCT,
    FOOD,
    CATALOG,

    // Lifestyle та інші
    LIFESTYLE,
    STREET,
    REPORTAGE,
    STUDIO,
    OUTDOOR,
    TRAVEL,

    // Домашні улюбленці
    PET,

    OTHER;
}

