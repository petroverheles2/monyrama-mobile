package com.monyrama.model;

import java.io.Serializable;

public class Server implements Serializable {
	private Integer serverid;
	private String alias;
	private String address;
	private Integer port;
	private String comment;
	
	public Integer getServerid() {
		return serverid;
	}
	
	public void setServerid(Integer serverid) {
		this.serverid = serverid;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return alias;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverid == null) ? 0 : serverid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Server other = (Server) obj;
		if (serverid == null) {
			if (other.serverid != null)
				return false;
		} else if (!serverid.equals(other.serverid))
			return false;
		return true;
	}

}
