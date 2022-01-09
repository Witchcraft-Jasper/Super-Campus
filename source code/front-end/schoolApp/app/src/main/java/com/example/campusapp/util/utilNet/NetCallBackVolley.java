package com.example.campusapp.util.utilNet;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public abstract class NetCallBackVolley implements Response.Listener<String>, Response.ErrorListener{
	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		onMyErrorResponse(error);
	}

	@Override
	public void onResponse(String response) {
		// TODO Auto-generated method stub

		onMyRespone(response);

	}

	/**
	 * 网络访问成功返回
	 * @param response 返回的结�?
	 */
	public abstract void onMyRespone(String response);
	/**
	 * 网络访问失败返回
	 * @param error 失败的原�?
	 */
	public abstract void onMyErrorResponse(VolleyError error);
}
