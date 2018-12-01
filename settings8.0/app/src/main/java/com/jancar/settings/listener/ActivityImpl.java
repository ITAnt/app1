/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jancar.settings.listener;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

/*
 * ================================================
 * 每个 {@link Activity} 都需要实现此类,以满足规范
 * ================================================
 */
public interface ActivityImpl {

	int initResid();
	IPresenter initPresenter();
	/**
	 * 初始化 View, 如果 {@link #initView(Bundle)} 返回 0, 框架则不会调用 {@link Activity#setContentView(int)}
	 *
	 * @param savedInstanceState
	 * @return
	 */
	int initView(@Nullable Bundle savedInstanceState);

	/**
	 * 初始化数据
	 *
	 * @param savedInstanceState
	 */
	void initData(@Nullable Bundle savedInstanceState);



	boolean useFragment();
}
