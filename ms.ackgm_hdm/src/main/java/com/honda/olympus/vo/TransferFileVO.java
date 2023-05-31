package com.honda.olympus.vo;

public class TransferFileVO {
	
	private Long status;

	private String msg;

	private String file;

	public TransferFileVO(Long status, String msg, String file) {
		super();
		this.status = status;
		this.msg = msg;
		this.file = file;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "TransferFileVO [status=" + status + ", msg=" + msg + ", file=" + file + "]";
	}
	
	
	
	

}
