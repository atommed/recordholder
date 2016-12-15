import play.api.http.DefaultHttpFilters
import play.filters.csrf.CSRFFilter
import javax.inject.Inject

class Filters @Inject() (csrf: CSRFFilter)
  extends DefaultHttpFilters(csrf){
}