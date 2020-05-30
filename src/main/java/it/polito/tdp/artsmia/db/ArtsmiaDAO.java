package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;


public class ArtsmiaDAO {

	public void loadArtObjects(Map<Integer,ArtObject> idMap) {
		
		String sql = "SELECT * from objects";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("object_id"))) {
					ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
							res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
							res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
							res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				idMap.put(artObj.getId(), artObj);
				}
				
			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Adiacenza> getAdiacenze(Map<Integer, ArtObject> idMap) {
		String sql = "SELECT eo1.object_id AS o1,eo2.object_id AS o2,COUNT(*) AS peso " + 
				"FROM exhibition_objects eo1,exhibition_objects eo2 " + 
				"WHERE eo1.exhibition_id=eo2.exhibition_id AND eo1.object_id>eo2.object_id " + 
				"GROUP BY eo1.object_id,eo2.object_id";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				ArtObject sorgente = idMap.get(rs.getInt("o1"));
				ArtObject destinazione = idMap.get(rs.getInt("o2"));
				
				if(sorgente != null && destinazione != null) {
					result.add(new Adiacenza(sorgente, destinazione, rs.getInt("peso")));
				} else{
					System.out.println("ERRORE IN GET ADIACENZA");
				}

			}
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
		return result;
	}
}
