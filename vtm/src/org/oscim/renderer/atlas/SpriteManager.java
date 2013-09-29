/*
 * Copyright 2013
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.renderer.atlas;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Canvas;
import org.oscim.renderer.atlas.TextureAtlas.Rect;
import org.oscim.renderer.elements.TextureItem;
import org.oscim.utils.pool.Inlist;

public abstract class SpriteManager<T> {

	public class Sprite extends Inlist<Sprite> {

		public Sprite(T i, TextureAtlas a, Rect r) {
			atlas = a;
			rect = r;
			item = i;
		}

		T item;
		TextureAtlas atlas;
		Rect rect;
	}

	TextureAtlas mAtlas;
	TextureAtlas curAtlas;

	Sprite items;

	protected final Canvas mCanvas = CanvasAdapter.g.getCanvas();
	protected TextureItem mTexture;

	public SpriteManager() {
		mTexture = TextureItem.get();

		//mTexture.ownBitmap = true;

		mAtlas = new TextureAtlas(
		                          TextureItem.TEXTURE_WIDTH,
		                          TextureItem.TEXTURE_HEIGHT,
		                          32);

		mCanvas.setBitmap(mTexture.bitmap);
	}

	public Sprite getRegion(T item) {
		//return items.get(item);
		for (Sprite t = items; t != null; t = t.next)
			if (t.item == item)
				return t;

		return null;
	}

	public void clear() {
		mTexture = TextureItem.pool.releaseAll(mTexture);
		mAtlas.clear();
		items = null;

		//mTexture.bitmap.eraseColor(Color.TRANSPARENT);
		mTexture = TextureItem.get();
		mCanvas.setBitmap(mTexture.bitmap);
	}

	public TextureItem getTextures() {
		return mTexture;
	}

	public Sprite addItem(T item, int width, int height) {
		Rect r = mAtlas.getRegion(width, height);
		if (r == null) {
			//create new atlas
			return null;
		}
		Sprite sprite = new Sprite(item, mAtlas, r);

		items = Inlist.appendItem(items, sprite);

		draw(item, r);

		return sprite;
	}

	abstract void draw(T item, Rect r);

}
