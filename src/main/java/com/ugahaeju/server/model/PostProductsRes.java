package com.ugahaeju.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.Access;

@Setter
@Getter
@AllArgsConstructor
public class PostProductsRes {
    public int code;
    public String message;
}
