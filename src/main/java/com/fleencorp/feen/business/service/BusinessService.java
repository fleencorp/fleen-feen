package com.fleencorp.feen.business.service;


import com.fleencorp.feen.business.exception.BusinessNotFoundException;
import com.fleencorp.feen.business.exception.BusinessNotOwnerException;
import com.fleencorp.feen.business.model.dto.AddBusinessDto;
import com.fleencorp.feen.business.model.dto.DeleteBusinessDto;
import com.fleencorp.feen.business.model.dto.UpdateBusinessDto;
import com.fleencorp.feen.business.model.response.BusinessAddResponse;
import com.fleencorp.feen.business.model.response.BusinessDeleteResponse;
import com.fleencorp.feen.business.model.response.BusinessUpdateResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface BusinessService {

  BusinessAddResponse addBusiness(AddBusinessDto addBusinessDto, final RegisteredUser user);

  BusinessUpdateResponse updateBusiness(Long businessId, UpdateBusinessDto updateBusinessDto, final RegisteredUser user)
    throws BusinessNotFoundException, BusinessNotOwnerException;

  BusinessDeleteResponse deleteBusiness(DeleteBusinessDto deleteBusinessDto, RegisteredUser user)
    throws BusinessNotFoundException, BusinessNotOwnerException;
}
