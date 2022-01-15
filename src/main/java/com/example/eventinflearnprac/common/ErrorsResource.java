package com.example.eventinflearnprac.common;

import com.example.eventinflearnprac.index.IndexController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {

    public static EntityModel<Errors> modelOf(Errors errors) {
        EntityModel<Errors> errorsModel = EntityModel.of(errors);
        errorsModel.add(linkTo(methodOn(IndexController.class)).withRel("index"));
        return errorsModel;
    }

}