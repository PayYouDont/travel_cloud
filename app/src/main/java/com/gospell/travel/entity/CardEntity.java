package com.gospell.travel.entity;

import android.view.View;

import lombok.Data;

@Data
public class CardEntity {
    private Integer imageResId;
    private String label;
    private View contentView;
}
