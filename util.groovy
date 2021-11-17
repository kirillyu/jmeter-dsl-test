import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class Util{
	class Data {
		String status
		ShowcaseView showcaseView  
	}

	class ShowcaseView {
		int version
		List<Catalog> catalog
	}
	class Catalog {
		String id
		String name
		String image
		List<String> products
		String parentId
		int rank
	}
    public static ArrayList productsGenerate(String body){
    		def gson = new Gson()
    		def input = gson.fromJson( body, Data )
    		def count = new Random().nextInt(7 - 3) + 3
    		def loop=0
    		def result = new ArrayList()
    		while (loop<count) {
				def category = input.showcaseView.catalog[new Random().nextInt(input.showcaseView.catalog.size())]
				System.out.println("category =" + category.toString())

				if (category.products.size()>0) {
					result.add(category.products[new Random().nextInt(category.products.size())])
					loop=loop+1
					System.out.println("result =" + category.toString())
				}
				
    		}
    		return result	
    }
}

props.put("Util",Util)
