package com.spring_base.fundamentals.service.cep;

import java.util.Map;

public interface CepFetcher {

    public Map<String, Object> fetch(String cep);

}
