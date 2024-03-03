package software.amazonaws.example.product.dao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.BatchExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.BatchExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import software.amazon.awssdk.services.rdsdata.model.UpdateResult;
import software.amazonaws.example.product.aurora.entity.Product;


public class AuroraServerlessV2DataApiDao {
	
	private static final RdsDataClient rdsDataClient = RdsDataClient.builder().build();
	
	private final String dbEndpoint = System.getenv("DB_ENDPOINT");
	private final String dbName = System.getenv("DB_NAME");
	private final String dbClusterArn = System.getenv("DB_CLUSTER_ARN");
	private final String dbSecretStoreArn = System.getenv("DB_CRED_SECRETS_STORE_ARN");
	
	
	public Optional<Product> getProductById(final String id) {
				
		final String sql="select id, name, price from tbl_product where id=:id";
		final SqlParameter sqlParam= SqlParameter.builder().name("id").value(Field.builder().longValue(Long.valueOf(id)).build()).build();
		System.out.println(" sql param "+sqlParam);
		final ExecuteStatementRequest request= ExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(sql).
				parameters(sqlParam).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse response= rdsDataClient.executeStatement(request);
		final List<List<Field>> records=response.records();
		
		if (records.isEmpty()) { 
			System.out.println("no records found");
			return Optional.empty();
		}
		
		System.out.println("response records: "+records);
		
		final List<Field> fields= records.get(0);
		final String name= fields.get(1).stringValue(); 
		final BigDecimal price= new BigDecimal(fields.get(2).stringValue());
		final Product product = new Product(Long.valueOf(id), name, price);
		System.out.println("Product :"+product);
		
		return Optional.of(product);
	}


	
	public List<Product> createProducts(List<Product> products) {
		
		
		if (products.size() ==1) {
			
			this.createProductTableAndSequence();
			return null;
		}
		
		
		final String CREATE_PRODUCT_SQL = "INSERT INTO tbl_product (id, name, price)"+
				"VALUES (:id, :name, :price);";			
		Set<Set<SqlParameter>> parameterSets= new HashSet<>();
		for(Product product : products) {	
			long productId= getNextSequenceValue("product_id");
			product.setId(productId);
			final SqlParameter productIdParam= SqlParameter.builder().name("id").value(Field.builder().longValue(Long.valueOf(productId)).build()).build();
			final SqlParameter productNameParam= SqlParameter.builder().name("name").value(Field.builder().stringValue(product.getName()).build()).build();
			final SqlParameter productPriceParam= SqlParameter.builder().name("price").value(Field.builder().doubleValue(product.getPrice().doubleValue()).build()).build();
			
			Set<SqlParameter> sqlParams= Set.of(productIdParam,productNameParam,productPriceParam);
			parameterSets.add(sqlParams);
		}
		final BatchExecuteStatementRequest request= BatchExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(CREATE_PRODUCT_SQL).
				parameterSets(parameterSets).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final BatchExecuteStatementResponse response= rdsDataClient.batchExecuteStatement(request);
		for(final UpdateResult updateResult: response.updateResults()) {
			System.out.println("update result " +updateResult.toString());
		}
		return products;
		
	}
	
	
		
	public void createProductTableAndSequence () {
		final String CREATE_USER_SEQUENCE_SQL= "CREATE SEQUENCE product_id START 1;"; 
		
	    final String CREATE_USER_TABLE_SQL = "CREATE TABLE tbl_product ( \n"+
			    "id bigint NOT NULL, \n"+
			    "name varchar(255) NOT NULL, \n"+
			    "price decimal NOT NULL, \n"+
			    "PRIMARY KEY (id)   \n"+ 
			");";
		
		this.createTableAndSequences(CREATE_USER_SEQUENCE_SQL);
		this.createTableAndSequences(CREATE_USER_TABLE_SQL);
		
	}

	
	
    private void createTableAndSequences(String sql) {

		System.out.println("dbEndpoint: "+dbEndpoint+ " dbName: "+dbName+ " dbclusterARN: "+dbClusterArn+ " dbSecretStoreARN: "+dbSecretStoreArn);
		System.out.println("execute sql "+sql);
		final ExecuteStatementRequest request= ExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(sql).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse response= rdsDataClient.executeStatement(request);
		final List<List<Field>> records=response.records();
		
		System.out.println("records "+records);
	}

	private long getNextSequenceValue(final String sequenceName) {
		
		final String NEXT_SEQUENCE_VAL_SQL="SELECT nextval('"+sequenceName+"');";

		System.out.println(" get next value for sequence: "+NEXT_SEQUENCE_VAL_SQL);
		//System.out.println("dbEndpoint: "+dbEndpoint+ " dbName: "+dbName+ " dbclusterARN: "+dbClusterArn+ " dbSecretStoreARN: "+dbSecretStoreArn);
		final ExecuteStatementRequest request= ExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(NEXT_SEQUENCE_VAL_SQL).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse response= rdsDataClient.executeStatement(request);
		final List<List<Field>> records=response.records();
		
		if (records.isEmpty()) { 
			System.out.println("no next sequence value found for sequence "+sequenceName);
			throw new RuntimeException("no next sequence value found for sequence "+sequenceName);
		}	
		System.out.println("response records: "+records);
	
		final List<Field> fields= records.get(0);
		final long sequenceNextValue= fields.get(0).longValue(); 
		System.out.println("next sequence value found for sequence "+sequenceName+ " "+sequenceNextValue);
		
		return sequenceNextValue;
	}
	
	}