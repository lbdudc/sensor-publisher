/*% if (feature.MWM_M_Client) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service;

import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
import es.udc.lbd.gema.lps.component.gema.model.repository.ClientRepository;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientFullDTO;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.spatial_specifications.WithinSpecification;
/*% if (feature.MWM_TE_VisitsRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientStatisticsDTO;
import java.time.LocalDate;
/*% } %*/
import es.udc.lbd.gema.lps.config.Properties;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
/*% if( feature.MWM_EmployeeAuthentication) { %*/
import org.springframework.security.access.prepost.PreAuthorize;
/*% } %*/
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
import org.springframework.beans.factory.annotation.Value;
/*% } %*/

@Service
@Transactional
public class ClientService {

  @Inject private ClientRepository clientRepository;

  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  @Value("${trajectory-exploitation.detailUrl:${properties.clientHost}/staff/clients/{id}}")
  private String clientDetailUrl;
  /*% } %*/

  @Inject private Properties properties;

  public Page<ClientDTO> getAll(
    Pageable pageable, List<String> filters, Specification idsSpecification) {

    Page<Client> page = null;

    if (idsSpecification != null) {
      page = clientRepository.findAll(idsSpecification, pageable);
    } else {
      page =
        clientRepository.findAll(
          SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }

    return page.map(client -> new ClientDTO(client/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, clientDetailUrl/*% } %*/));
  }

  public List<ClientDTO> findAll(Specification spec, Geometry bbox) {
    // Fixing SRID
    if (bbox != null) {
      bbox.setSRID(properties.getGis().getDefaultSrid());
    }

    Specification bboxSpec = new WithinSpecification(bbox, "location");

    // We only want active clients
    GenericSpecification genericSpecification = new GenericSpecification();
    genericSpecification.add(
      new SearchCriteria("active", true, SearchOperation.EQUAL, LogicalOperation.AND));

    List<Client> result =
      clientRepository.findAll(genericSpecification.and(bboxSpec).and(spec), Sort.by("fullName"));
    return result.stream().map(client -> new ClientDTO(client)).collect(Collectors.toList());
  }

  public ClientFullDTO findById(Long id) {
    Client client = clientRepository.findById(id).orElse(null);
    if (client != null) {
      return new ClientFullDTO(client/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, clientDetailUrl/*% } %*/);
    } else {
      return null;
    }
  }

  /*% if (feature.MWM_VisitSchedule || feature.MWM_PM_Clients) { %*/
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public ClientFullDTO createClient(ClientFullDTO clientDTO) {
    Client client = new Client();

    client.setEmail(clientDTO.getEmail());
    client.setFullName(clientDTO.getFullName());
    client.setLabel(clientDTO.getLabel());
    client.setLocation(clientDTO.getLocation());
    client.setPhone(clientDTO.getPhone());
    client.setAddress(clientDTO.getAddress());
    client.setActive(true); // Active by default

    // Fixing location SRID
    if (client.getLocation() != null)
      client.getLocation().setSRID(properties.getGis().getDefaultSrid());

    Client clientBd = clientRepository.save(client);
    return new ClientFullDTO(clientBd/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, clientDetailUrl/*% } %*/);
  }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public ClientFullDTO updateClient(ClientFullDTO clientDTO) {
    Client client = clientRepository.findById(clientDTO.getId()).orElse(null);

    if (client == null) {
      return null;
    }

    client.setEmail(clientDTO.getEmail());
    client.setFullName(clientDTO.getFullName());
    client.setLabel(clientDTO.getLabel());
    client.setLocation(clientDTO.getLocation());
    client.setPhone(clientDTO.getPhone());
    client.setAddress(clientDTO.getAddress());
    //client.active can't be mutated outside activate/delete methods

    // Fixing location SRID
    if (client.getLocation() != null)
      client.getLocation().setSRID(properties.getGis().getDefaultSrid());

    Client clientBd = clientRepository.save(client);
    return new ClientFullDTO(clientBd/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, clientDetailUrl/*% } %*/);
  }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public Long delete(Long id) {
    Client client = clientRepository.findById(id).orElse(null);
    if (client != null) {
      client.setActive(false); // Logic delete
      clientRepository.save(client);
      return id;
    } else {
      return null;
    }
  }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public Long activate(Long id) {
    Client client = clientRepository.findById(id).orElse(null);
    if (client != null) {
      client.setActive(true);
      clientRepository.save(client);
      return id;
    } else {
      return null;
    }
  }
  /*% } %*/
  /*% if (feature.MWM_TE_VisitsRecord) { %*/
  public List<ClientDTO> findByHasVisitsBetween(LocalDate start, LocalDate end, PlannedEventState state) {
    List<Client> clients = clientRepository.findByHasVisitsBetween(start, end, state);
    List<ClientDTO> clientsDTO =
        clients.stream().map(client -> new ClientDTO(client/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, clientDetailUrl/*% } %*/)).collect(Collectors.toList());
    return clientsDTO;
  }

  public List<ClientStatisticsDTO> getVisitsStatistics(List<Long> clients, List<Long> employees, LocalDate start, LocalDate end, String type) {
    if (type != null && type.equalsIgnoreCase("In")) end = start;

    return clientRepository.getVisitsStatistics(clients, employees, start, end, PlannedEventState.DONE);
  }
  /*% } %*/
}
/*% } %*/
