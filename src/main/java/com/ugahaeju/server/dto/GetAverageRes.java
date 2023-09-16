package com.ugahaeju.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAverageRes {
    public int code;
    public String message;
    Average topAverage;
    Average myAverage;
}
