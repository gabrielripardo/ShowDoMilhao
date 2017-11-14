package model.sqlite;
/**
* @descrition Classe Dao que realiza toda a manipulação pura com o banco de dados.
* @date 10-24-2017
* @author Gabriel Ripardo
*/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.game.Participante;

public class ParticipanteDAO {
private Connection conexao;
private boolean isEmpty;

	public ParticipanteDAO(){
		conexao = null;
		isEmpty = true;
		criarTables();
	}
	public void abrirConexao() {
		if(this.conexao == null){
			this.conexao = new ConnectionAgenda().getConnection();
		}
	}
	void criarTables() {
		try {                              

			this.abrirConexao();
			String sql1 = "create table if not exists perguntas(id integer primary key not null, pergunta varchar(60), numero1 varchar(15), numero2 varchar(15) , numero3 varchar(15), numero4 varchar(15), resposta int)";
			PreparedStatement stmt1 = this.conexao.prepareStatement(sql1);
			String sql2 = "create table if not exists participantes(id integer primary key not null, nome varchar(20), pontos int)";
			PreparedStatement stmt2 = this.conexao.prepareStatement(sql2);
			
			stmt1.execute();
			stmt1.close();
			stmt2.execute();
			stmt2.close();
			isEmpty = false;
		}catch(SQLException e) {
			System.out.print("Não foi possível criar campos! "+e);
		}finally {
			this.fecharConexao();
		}
	}

	public boolean adicionarJogador(Participante player) {
		try{
			this.abrirConexao();
			String sql = "INSERT INTO participantes (nome, pontos) VALUES (?, ?)";
			PreparedStatement stmt = this.conexao.prepareStatement(sql);
			
			stmt.setString(1, player.getNome());
			stmt.setInt(2, player.getPontos());
			stmt.execute();
			stmt.close();
			
			return true;
		}catch(SQLException e){
			System.out.print("Erro ao adicionar participante! "+e);
		}finally {
			this.fecharConexao();
		}
		return false;
	}
	
	public List<Participante> listarRanking(){
		List<Participante> melhores = new ArrayList<Participante>();
		try {
			abrirConexao();
			String sqlSelect = "SELECT * FROM participantes ORDER BY pontos DESC LIMIT 10";
			PreparedStatement stmt = this.conexao.prepareStatement(sqlSelect);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				Participante topPessoa = new Participante();
				
				topPessoa.setNome(rs.getString("NOME"));
				topPessoa.setPontos(rs.getInt("PONTOS"));
				melhores.add(topPessoa); 
			} 
			rs.close();
			stmt.close();
		}catch(SQLException e) {
			System.out.print("Ocorrreu um erro ao rankear os participantes!");
		}
		finally {
			this.fecharConexao();
		}
		return melhores;
	}
	public void fecharConexao() {
		try{
			if(this.conexao != null){
				this.conexao.close();
				this.conexao = null;
			}
		}catch(SQLException e){
			if(isEmpty) {
				this.criarTables();
				this.fecharConexao();
			}
		}
	}
}