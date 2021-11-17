def sb = new StringBuilder()
def Ut = props.get("Util")
System.out.println("Class =" + Ut.toString())
def result = Ut.productsGenerate(prev.getResponseDataAsString())
for (def i=0; i<result.size();i++){
	def quantity_r = new Random().nextInt(3)+1
	sb.append('''"''').append(result.get(i)).append('''"''').append(",")
	System.out.println("SB =" + sb.toString())
}

props.put("PRODUCTS_PAYLOAD",sb.toString().replaceFirst(/.$/,""))