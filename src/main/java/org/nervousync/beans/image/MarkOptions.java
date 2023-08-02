/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.beans.image;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.nervousync.commons.Globals;

/**
 * <h2 class="en">Mark options of image mark operate</h2>
 * <h2 class="zh-CN">用于图片水印操作的剪切选项</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: May 1, 2018 16:11:01 $
 */
public final class MarkOptions {

	/**
	 * <h3 class="en">Mark type. Value of MarkOptions.MarkType enumeration</h3>
	 * <h3 class="zh-CN">水印类型，MarkOptions.MarkType的枚举值</h3>
	 * @see MarkOptions.MarkType
	 */
	private final MarkType markType;
	/**
	 * <h3 class="en">Mark location. Instance of MarkOptions.MarkLocation</h3>
	 * <h3 class="zh-CN">水印位置，MarkOptions.MarkLocation实例对象</h3>
	 * @see MarkOptions.MarkLocation
	 */
	private final MarkLocation markLocation;
	/**
	 * <h3 class="en">Mark image path. Only using when markType is MarkType.ICON</h3>
	 * <h3 class="zh-CN">水印图片地址，仅当markType值为MarkType.ICON时有效</h3>
	 */
	private final String markPath;
	/**
	 * <h3 class="en">Transparent value of mark image. default is 1, valid value is between 0 and 1</h3>
	 * <h3 class="zh-CN">水印图片的透明度，默认值为1，有效值在0到1之间</h3>
	 */
	private final float transparency;
	/**
	 * <h3 class="en">Mark text value. Only using when markType is MarkType.TEXT</h3>
	 * <h3 class="zh-CN">水印文字，仅当markType值为MarkType.TEXT时有效</h3>
	 */
	private final String markText;
	/**
	 * <h3 class="en">Mark text color settings.</h3>
	 * <h3 class="zh-CN">水印文字的颜色值</h3>
	 */
	private final Color color;
	/**
	 * <h3 class="en">Mark text font name settings.</h3>
	 * <h3 class="zh-CN">水印文字的字体名</h3>
	 */
	private final String fontName;
	/**
	 * <h3 class="en">Mark text font size settings.</h3>
	 * <h3 class="zh-CN">水印文字的字号</h3>
	 */
	private final int fontSize;
	/**
	 * <h3 class="en">Constructor for MarkOptions</h3>
	 * <h3 class="zh-CN">CutOptions的构造方法</h3>
	 *
	 * @param markType		<span class="en">Mark type. Value of MarkOptions.MarkType enumeration</span>
	 *                      <span class="zh-CN">水印类型，MarkOptions.MarkType的枚举值</span>
	 * @param markLocation  <span class="en">Mark location. Instance of MarkOptions.MarkLocation</span>
	 * 	 *                  <span class="zh-CN">水印位置，MarkOptions.MarkLocation实例对象</span>
	 * @param markPath		<span class="en">Mark image path. Only using when markType is MarkType.ICON</span>
	 *                      <span class="zh-CN">水印图片地址，仅当markType值为MarkType.ICON时有效</span>
	 * @param transparency	<span class="en">Transparent value of mark image. default is 1, valid value is between 0 and 1</span>
	 *                      <span class="zh-CN">水印图片的透明度，默认值为1，有效值在0到1之间</span>
	 * @param markText		<span class="en">Mark text value. Only using when markType is MarkType.TEXT</span>
	 *                      <span class="zh-CN">水印文字，仅当markType值为MarkType.TEXT时有效</span>
	 * @param color			<span class="en">Mark text color settings.</span>
	 *                      <span class="zh-CN">水印文字的颜色值</span>
	 * @param fontName		<span class="en">Mark text font name settings.</span>
	 *                      <span class="zh-CN">水印文字的字体名</span>
	 * @param fontSize		<span class="en">Mark text font size settings.</span>
	 *                      <span class="zh-CN">水印文字的字号</span>
	 */
	private MarkOptions(final MarkType markType, final MarkLocation markLocation, final String markPath,
	                    final float transparency, final String markText,
	                    final Color color, final String fontName, final int fontSize) {
		this.markType = markType;
		this.markLocation = markLocation;
		this.markPath = markPath;
		this.transparency = transparency;
		this.markText = markText;
		this.color = color;
		this.fontName = fontName;
		this.fontSize = fontSize;
	}
	/**
	 * <h3 class="en">Static method for initialize Icon MarkOptions</h3>
	 * <h3 class="zh-CN">用于初始化图片水印MarkOptions的静态方法</h3>
	 *
	 * @param markLocation  <span class="en">Mark location. Instance of MarkOptions.MarkLocation</span>
	 * 	 *                  <span class="zh-CN">水印位置，MarkOptions.MarkLocation实例对象</span>
	 * @param markPath		<span class="en">Mark image path. Only using when markType is MarkType.ICON</span>
	 *                      <span class="zh-CN">水印图片地址，仅当markType值为MarkType.ICON时有效</span>
	 * @param transparency	<span class="en">Transparent value of mark image. default is 1, valid value is between 0 and 1</span>
	 *                      <span class="zh-CN">水印图片的透明度，默认值为1，有效值在0到1之间</span>
	 * @return	<span class="en">Initialized MarkOptions instance</span>
	 * 			<span class="en">初始化的MarkOptions实例对象</span>
	 */
	public static MarkOptions markIcon(final MarkLocation markLocation, final String markPath,
	                                   final float transparency) {
		return new MarkOptions(MarkType.ICON, markLocation, markPath, transparency, null, null,
				null, Globals.DEFAULT_VALUE_INT);
	}
	/**
	 * <h3 class="en">Static method for initialize Text MarkOptions</h3>
	 * <h3 class="zh-CN">用于初始化文字水印MarkOptions的静态方法</h3>
	 * Initialize TEXT MarkOptions
	 *
	 * @param markLocation  <span class="en">Mark location. Instance of MarkOptions.MarkLocation</span>
	 * 	 *                  <span class="zh-CN">水印位置，MarkOptions.MarkLocation实例对象</span>
	 * @param markText		<span class="en">Mark text value. Only using when markType is MarkType.TEXT</span>
	 *                      <span class="zh-CN">水印文字，仅当markType值为MarkType.TEXT时有效</span>
	 * @param color			<span class="en">Mark text color settings.</span>
	 *                      <span class="zh-CN">水印文字的颜色值</span>
	 * @param fontName		<span class="en">Mark text font name settings.</span>
	 *                      <span class="zh-CN">水印文字的字体名</span>
	 * @param fontSize		<span class="en">Mark text font size settings.</span>
	 *                      <span class="zh-CN">水印文字的字号</span>
	 * @return	<span class="en">Initialized MarkOptions instance</span>
	 * 			<span class="en">初始化的MarkOptions实例对象</span>
	 */
	public static MarkOptions markText(final MarkLocation markLocation, final String markText,
	                                   final Color color, final String fontName, final int fontSize) {
		return new MarkOptions(MarkType.TEXT, markLocation, null, Globals.DEFAULT_VALUE_FLOAT,
				markText, color, fontName, fontSize);
	}
	/**
	 * <h3 class="en">Getter method for mark type</h3>
	 * <h3 class="zh-CN">水印类型的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark type</span>
	 *            <span class="zh-CN">水印类型值</span>
	 */
	public MarkType getMarkType() {
		return markType;
	}
	/**
	 * <h3 class="en">Getter method for mark location</h3>
	 * <h3 class="zh-CN">水印坐标的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark location</span>
	 *            <span class="zh-CN">水印坐标实例对象</span>
	 */
	public MarkLocation getMarkLocation() {
		return markLocation;
	}
	/**
	 * <h3 class="en">Getter method for mark image path</h3>
	 * <h3 class="zh-CN">水印图片地址的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark image path</span>
	 *            <span class="zh-CN">水印图片地址</span>
	 */
	public String getMarkPath() {
		return markPath;
	}
	/**
	 * <h3 class="en">Getter method for mark image transparency value</h3>
	 * <h3 class="zh-CN">水印透明度值的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark image transparency value</span>
	 *            <span class="zh-CN">水印透明度值</span>
	 */
	public float getTransparency() {
		return transparency;
	}
	/**
	 * <h3 class="en">Getter method for mark text value</h3>
	 * <h3 class="zh-CN">水印文字值的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark text value</span>
	 *            <span class="zh-CN">水印文字值</span>
	 */
	public String getMarkText() {
		return markText;
	}
	/**
	 * <h3 class="en">Getter method for mark text color value</h3>
	 * <h3 class="zh-CN">水印文字颜色值的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark text color value</span>
	 *            <span class="zh-CN">水印文字颜色值</span>
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * <h3 class="en">Getter method for mark text font name</h3>
	 * <h3 class="zh-CN">水印文字字体名称的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark text font name</span>
	 *            <span class="zh-CN">水印文字字体名称</span>
	 */
	public String getFontName() {
		return fontName;
	}
	/**
	 * <h3 class="en">Getter method for mark text font size</h3>
	 * <h3 class="zh-CN">水印文字字号的Getter方法</h3>
	 *
	 * @return    <span class="en">Value of mark text font size</span>
	 *            <span class="zh-CN">水印文字字号</span>
	 */
	public int getFontSize() {
		return fontSize;
	}
	/**
	 * <h3 class="en">Calculate and generate MarkPosition by given image width and height</h3>
	 * <h3 class="zh-CN">根据给定的图片尺寸，计算并生成水印位置的MarkPosition实例对象</h3>
	 *
	 * @param width		<span class="en">Image width value</span>
	 *                  <span class="zh-CN">图片的宽度</span>
	 * @param height	<span class="en">Image height value</span>
	 *                  <span class="zh-CN">图片的高度</span>
	 * @return	<span class="en">Initialized MarkPosition instance</span>
	 * 			<span class="en">初始化的MarkPosition实例对象</span>
	 */
	public MarkPosition retrievePosition(int width, int height) {
		int positionX = Globals.DEFAULT_VALUE_INT;
		int positionY = Globals.DEFAULT_VALUE_INT;
		switch (this.markType) {
			case ICON -> {
				ImageIcon imageIcon = new ImageIcon(this.markPath);
				Image iconImg = imageIcon.getImage();
				if (iconImg != null && this.transparency >= 0 && this.transparency <= 1) {
					switch (this.markLocation) {
						case LEFT_TOP -> {
							positionX = Globals.INITIALIZE_INT_VALUE;
							positionY = Globals.INITIALIZE_INT_VALUE;
						}
						case TOP -> {
							positionX = (width - iconImg.getWidth(null)) / 2;
							positionY = Globals.INITIALIZE_INT_VALUE;
						}
						case RIGHT_TOP -> {
							positionX = width - iconImg.getWidth(null);
							positionY = Globals.INITIALIZE_INT_VALUE;
						}
						case LEFT -> {
							positionX = Globals.INITIALIZE_INT_VALUE;
							positionY = (height - iconImg.getHeight(null)) / 2;
						}
						case CENTER -> {
							positionX = (width - iconImg.getWidth(null)) / 2;
							positionY = (height - iconImg.getHeight(null)) / 2;
						}
						case RIGHT -> {
							positionX = width - iconImg.getWidth(null);
							positionY = (height - iconImg.getHeight(null)) / 2;
						}
						case LEFT_BOTTOM -> {
							positionX = 0;
							positionY = height - iconImg.getHeight(null);
						}
						case BOTTOM -> {
							positionX = (width - iconImg.getWidth(null)) / 2;
							positionY = height - iconImg.getHeight(null);
						}
						case RIGHT_BOTTOM -> {
							positionX = width - iconImg.getWidth(null);
							positionY = height - iconImg.getHeight(null);
						}
					}
				}
			}
			case TEXT -> {
				if (this.markText != null && this.fontName != null
						&& this.fontSize > 0) {
					int textWidth = this.markText.length() * this.fontSize;
					int textHeight = this.fontSize;

					switch (this.markLocation) {
						case LEFT_TOP -> {
							positionX = 0;
							positionY = textHeight;
						}
						case TOP -> {
							positionX = (width - textWidth) / 2;
							positionY = textHeight;
						}
						case RIGHT_TOP -> {
							positionX = width - textWidth;
							positionY = textHeight;
						}
						case LEFT -> {
							positionX = 0;
							positionY = (height + textHeight) / 2;
						}
						case CENTER -> {
							positionX = (width - textWidth) / 2;
							positionY = (height + textHeight) / 2;
						}
						case RIGHT -> {
							positionX = width - textWidth;
							positionY = (height + textHeight) / 2;
						}
						case LEFT_BOTTOM -> {
							positionX = 0;
							positionY = height;
						}
						case BOTTOM -> {
							positionX = (width - textWidth) / 2;
							positionY = height;
						}
						case RIGHT_BOTTOM -> {
							positionX = width - textWidth;
							positionY = height;
						}
					}
				}
			}
		}
		if (positionX != Globals.DEFAULT_VALUE_INT && positionY != Globals.DEFAULT_VALUE_INT) {
			return new MarkPosition(positionX, positionY);
		}
		return null;
	}

	/**
	 * <h2 class="en">MarkPosition define</h2>
	 * <h2 class="zh-CN">MarkPosition定义</h2>
	 *
	 * @param positionX <h3 class="en">Mark position value X</h3>
	 *                  <h3 class="zh-CN">水印起始X坐标</h3>
	 * @param positionY <h3 class="en">Mark position value Y</h3>
	 *                  <h3 class="zh-CN">水印起始Y坐标</h3>
	 */
		public record MarkPosition(int positionX, int positionY) {
		/**
		 * <h3 class="en">Constructor for MarkPosition</h3>
		 * <h3 class="zh-CN">MarkPosition构造方法</h3>
		 *
		 * @param positionX <span class="en">Mark position value X</span>
		 *                  <span class="zh-CN">水印起始X坐标</span>
		 * @param positionY <span class="en">Mark position value Y</span>
		 *                  <span class="zh-CN">水印起始Y坐标</span>
		 */
		public MarkPosition {
		}

		/**
		 * <h3 class="en">Getter method for position X</h3>
		 * <h3 class="zh-CN">起始X坐标的Getter方法</h3>
		 *
		 * @return <span class="en">Value of begin position X</span>
		 * <span class="zh-CN">起始X坐标值</span>
		 */
			@Override
			public int positionX() {
				return positionX;
			}

		/**
		 * <h3 class="en">Getter method for position Y</h3>
		 * <h3 class="zh-CN">起始Y坐标的Getter方法</h3>
		 *
		 * @return <span class="en">Value of begin position Y</span>
		 * <span class="zh-CN">起始Y坐标值</span>
		 */
			@Override
			public int positionY() {
				return positionY;
			}
		}

	/**
	 * <h2 class="en">Enumeration define for MarkType</h2>
	 * <h2 class="zh-CN">MarkType枚举类定义</h2>
	 */
	public enum MarkType {
		/**
         * <h3 class="en">Icon mark</h3>
         * <h3 class="zh-CN">图片水印</h3>
		 */
		ICON,
		/**
         * <h3 class="en">Text mark</h3>
         * <h3 class="zh-CN">文字水印</h3>
		 */
		TEXT
	}

	/**
	 * <h2 class="en">Enumeration define for MarkLocation</h2>
	 * <h2 class="zh-CN">MarkLocation枚举类定义</h2>
	 */
	public enum MarkLocation {
		/**
         * <span class="en">Location: Top and Left</span>
         * <span class="zh-CN">水印位置：左上方</span>
		 */
		LEFT_TOP,
		/**
         * <span class="en">Location: Top and middle</span>
         * <span class="zh-CN">水印位置：正上方</span>
		 */
		TOP,
		/**
         * <span class="en">Location: Top and Right</span>
         * <span class="zh-CN">水印位置：右上方</span>
		 */
		RIGHT_TOP,
		/**
         * <span class="en">Location: Middle and Left</span>
         * <span class="zh-CN">水印位置：左侧垂直居中</span>
		 */
		LEFT,
		/**
         * <span class="en">Location: Center</span>
         * <span class="zh-CN">水印位置：正中间</span>
		 */
		CENTER,
		/**
         * <span class="en">Location: Middle and Right</span>
         * <span class="zh-CN">水印位置：右侧垂直居中</span>
		 */
		RIGHT,
		/**
         * <span class="en">Location: Bottom and Left</span>
         * <span class="zh-CN">水印位置：左下方</span>
		 */
		LEFT_BOTTOM,
		/**
         * <span class="en">Location: Bottom and Middle</span>
         * <span class="zh-CN">水印位置：下方居中</span>
		 */
		BOTTOM,
		/**
         * <span class="en">Location: Bottom and Right</span>
         * <span class="zh-CN">水印位置：右下方</span>
		 */
		RIGHT_BOTTOM
	}
}
