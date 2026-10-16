package com.visit.jw_ls_maps_visit.service;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.*;
import java.time.*;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visit.jw_ls_maps_visit.model.PopulationReference;
import com.visit.jw_ls_maps_visit.repository.PopulationReferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class PopulationRefreshService {
 private static final Logger LOG=LoggerFactory.getLogger(PopulationRefreshService.class);
 private final PopulationReferenceRepository repository;
 private final HttpClient client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
 @Value("${app.population.auto-enabled:true}") private boolean enabled;
 @Value("${app.population.query-url:https://query.wikidata.org/sparql}") private String queryUrl;
 public PopulationRefreshService(PopulationReferenceRepository repository){this.repository=repository;}
 @Scheduled(initialDelay=10000,fixedDelayString="${app.population.refresh-ms:86400000}")
 public void refresh(){
  if(!enabled)return;
  try {
   String query="""
    PREFIX wd: <http://www.wikidata.org/entity/>
    PREFIX wdt: <http://www.wikidata.org/prop/direct/>
    PREFIX p: <http://www.wikidata.org/prop/>
    PREFIX ps: <http://www.wikidata.org/prop/statement/>
    PREFIX pq: <http://www.wikidata.org/prop/qualifier/>
    PREFIX wikibase: <http://wikiba.se/ontology#>
    PREFIX bd: <http://www.bigdata.com/rdf#>
    PREFIX prov: <http://www.w3.org/ns/prov#>
    SELECT DISTINCT ?item ?itemLabel ?population ?date WHERE {
     ?item wdt:P17 wd:Q1029; wdt:P31/wdt:P279* wd:Q515; p:P1082 ?statement.
     ?statement ps:P1082 ?population; pq:P585 ?date; prov:wasDerivedFrom ?reference.
     FILTER NOT EXISTS { ?statement wikibase:rank wikibase:DeprecatedRank }
     FILTER(?population >= 0 && ?date <= NOW())
     SERVICE wikibase:label { bd:serviceParam wikibase:language "pt,en". }
    } LIMIT 3000
    """;
   URI uri=URI.create(queryUrl+"?format=json&query="+URLEncoder.encode(query,StandardCharsets.UTF_8));
   var request=HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(35))
    .header("Accept","application/sparql-results+json").header("User-Agent","LSMapsVisit/1.0 PopulationReferenceUpdater").GET().build();
   var response=client.send(request,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()!=200)throw new IllegalStateException("HTTP "+response.statusCode());
   var rows=new ObjectMapper().readTree(response.body()).path("results").path("bindings");
   if(!rows.isArray())throw new IllegalStateException("Invalid population response");
   for(var row:rows){
    try {
     String item=row.path("item").path("value").asText();
     String name=row.path("itemLabel").path("value").asText();
     String date=row.path("date").path("value").asText();
     if(!item.matches("http://www.wikidata.org/entity/Q[0-9]+") || name.isBlank() || !date.matches("[0-9]{4}-.*"))continue;
     int year=Integer.parseInt(date.substring(0,4));
     double numeric=Double.parseDouble(row.path("population").path("value").asText());
     if(year<2000 || year>Year.now().getValue() || !Double.isFinite(numeric) || numeric<0 || numeric>100000000 || numeric!=Math.rint(numeric))continue;
     String id=item.substring(item.lastIndexOf('/')+1);
     var record=repository.findById(id).orElseGet(PopulationReference::new);
     if(record.getYear()!=null && record.getYear()>year)continue;
     record.setId(id);record.setCity(name);record.setPopulation((long)numeric);record.setYear(year);
     record.setScope("city");record.setSource("https://www.wikidata.org/wiki/"+id+"#P1082");
     record.setCheckedAt(Instant.now().toString());repository.save(record);
    } catch(RuntimeException invalid){ LOG.debug("Ignored invalid population record",invalid); }
   }
   LOG.info("Automatic population reference refresh completed");
  }catch(InterruptedException interrupted){Thread.currentThread().interrupt();}
   catch(Exception error){LOG.warn("Population source unavailable; preserving last saved references: {}",error.getMessage());}
 }
}
