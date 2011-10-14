MultiLevelSelector {
	var <mlDict, <selections=#[], <>idle=false, noUpd=false, isChanged=false;
	*new{|sources, initSelections, whiteFuncs| 
		^super.new.init(sources, initSelections, whiteFuncs) 
	}
	storeArgs { ^this.decomposeSources }
	copy { ^MultiLevelSelector(*this.decomposeSources) }
	deepCopy { ^this.asCompileString.interpret }
	init {|srcs, initSels, wFuncs|
		if (srcs.isKindOf(MultiLevelIdentityDictionary)) { mlDict = srcs } {
			mlDict = MultiLevelIdentityDictionary.new;
			if (srcs.notNil) { this.prAddLeavesR(List.new, srcs, initSels, wFuncs); this.selected }
		}
//;mlDict.postTree;
	}
	getSelector{|src, selIndex, wFunc, selByIndex=true|
		var selector;
		if (src.isInteger) { src = Array.series(src, 0, 1) };
		if (selByIndex.not) { selIndex = src.indexOf(selIndex) };
		selector = IdentitySelector(src, selIndex, wFunc);
		selector.addDependant(this);
		^selector
	}
	prAddLeavesR{|path, srcs, initSels, wFuncs, selByIndex=true|
		initSels = initSels ?? {[nil,[]]};
		wFuncs = wFuncs ?? {[nil,[]]}; 
//this.logln("path, srcs, initSels" + [path, srcs, initSels, wFuncs], lfB:2);
		mlDict.put(*path ++ [\selector, 
			this.getSelector(srcs[0], initSels[0], wFuncs[0], selByIndex) ]);
		if (srcs.size >1) { srcs[0].do{|key, i| 
			if (srcs[1][i].notNil) {
			this.prAddLeavesR(path ++ [key], srcs.drop(1)[0][i], 
				initSels.drop(1)[0][i], wFuncs.drop(1)[0][i], selByIndex ) } 
		}}
	}
	decomposeSources{
		^this.doDecomposeSources([], mlDict.dictionary)
	}
	doDecomposeSources {|path, object|
		var sel, sources, parentSrc, currSrcs, selections, currSelects, whiteFuncs, currWhites;
		// if (object.isKindOf(Dictionary)) {
			sel = object[\selector]; // object.removeAt(\selector);
			parentSrc = sel.src.copy;
			
			currSrcs = Array.newClear(parentSrc.size);
			currSelects = Array.newClear(parentSrc.size);
			currWhites = Array.newClear(parentSrc.size);
			
			sources = [parentSrc];
			selections = [sel.wrapIndex];
			whiteFuncs = [sel.whiteFunc];
			
			object.keysValuesDo({|name, subobject|
				var index, srcs, selects, whites;
				if (name != \selector) {
					index = parentSrc.indexOf(name);
			//this.logln("name:" + [name, parentSrc]);
			//this.logln("index:" + index);
					#srcs, selects, whites = this.doDecomposeSources(path ++ [name], subobject);
			//this.logln("srcs:" + srcs); 
					currSrcs.put(index, srcs);
					currSelects.put(index, selects);
					currWhites.put(index, whites);
			//this.logln("index, currSrcs:" + [index, currSrcs]);
				}		
			});
			//sources.removeAllSuchThat({arg item; item.isNil});
	//this.logln("currSrcs:" + currSrcs, lfB:2);
	//this.logln("sources:" + sources);
		//currSrcs.removeAllSuchThat({arg item; item.isNil});
			//currSrcs.removeAllSuchThat({arg item; item.every{|thing| thing.isNil}});
			//if (currSrcs.notEmpty) { }
			if (currSrcs.every{|thing| thing.isNil}.not) {
				sources = sources.add(currSrcs);
				selections = selections.add(currSelects);
				whiteFuncs = whiteFuncs.add(currWhites);
			};
			
			// sources.removeAllSuchThat({arg item; item.every{|thing| thing.isNil}});
	//this.logln("currSrcsA:" + currSrcs);
	//this.logln("sourcesA:" + sources);
			^[sources, selections, whiteFuncs]
		// }
	}
	
//	getSources{|list|
//		^list.collect{|item| if(item.isKindOf(Collection)) { this.getSources(item) }{ 
//			item.src.postln } }
//	}
	selPath{|level ...path| //this.logln("selPath:" + [level, path])
		^this.prSelPathR(path.size, level ? -1, path).drop(path.size);
	}
	prSelPathR{|i, level, path|
		var selector = mlDict.at(*path++[\selector]);
//this.logln("i, level, path, selector" + [i, level, path, selector]);
		if ((selector.notNil) && ((i < (level+1)) || (level == -1))) {
			^this.prSelPathR(i+1, level, path++[selector.selected]) 
		}{ ^path }
	}
	selected{|level|
		var res = 
		if (idle) { //quick look up
			if (level.notNil) { selections.keep(level+1) } { selections }
		}{
			if (level.notNil) { this.selPath(level) } { idle=true; selections = this.selPath }
		};
		^if (level.isNil) {res} {res ++ Array.newClear(level+1 - res.size) }
	}
	selectedAt{|...path| 
		var selector = this.selectorAt(*path);
		^if (selector.notNil) { selector.selected }{ nil }
	}
	
	selector{|level| ^mlDict.at(*(this.selected(level).drop(-1))++[\selector]) }
	selectorAt{|...path| ^this.selectorAtPath(path) }
	selectorAtPath{|path| ^mlDict.at(*path++[\selector])}
	
	sourceAt{|...path| ^this.sourceAtPath(path) }
	sourceAtPath {|path|
		var sel = this.selectorAtPath(path);
		^sel !? {sel.src}
	}
	
	removeAt{|...path|
		var item = path.last;
		var selector = this.selectorAt(*path.drop(-1));
		var removed;
		if (selector.isNil) {^nil};
		if (selector.selected == item) { idle = false };
		removed = selector.remove(item);
		mlDict.removeEmptyAt(*path);
		^removed
	}
	putAt{|path, sources, initSelections, whiteFuncs| // puts and overwrites at path
		var selector;
		if ((path.size-1) >= 0) {
			selector = this.selectorAt(*path.drop(-1)); // nil.add() exists in SC !!! be aware
			if (selector.isNil) { ("¥¥¥Êno parent selector -> can't putAt path" + path).throw };
			selector.add(path.last, selector.selected == path.last);
		};
		this.prAddLeavesR(path, sources, initSelections, whiteFuncs, false);
		idle = false;
	}
	addAllAt{|path, items, selectItem| // at to selector of path, if none there, putAt
		var selector = this.selectorAt(*path);
		if (selector.isNil) { 
			this.putAt(path, [items], [selectItem]); 
			if (selectItem.notNil) {idle = false}
		}{ if (selector.addAll(items, selectItem).notNil) { idle = false } }
	}
	addAll{|level, items, selectItem|
		this.addAllAt(this.selected(level).drop(-1), items, selectItem)
	}
	addPutAt{|path, sources, initSelections, whiteFuncs, isSel=false| // add parent, put path
		var item = path.last;
		this.addAllAt(path.drop(-1), [item], if (isSel) {item} {nil});
		if (this.selectorAt(*path.drop(-1)).src.includes(item).not) {
			("" + item + "filtered out by" + path.drop(-1)).warn;
		}{
			this.prAddLeavesR(path, sources, initSelections, whiteFuncs, false);
			if (isSel) { this.selectAt(path.drop(-1)) };
		}
	}
	addPut{|level, item, sources, initSelections, whiteFuncs, isSel=false|
		this.addPutAt(this.selected(level).drop(-1), item, 
			sources, initSelections, whiteFuncs, isSel)
	}
	
	selectAt{|path, item|
		noUpd=true;
		^if (this.selectorAt(*path).select(item).notNil) {
			if (path != this.selected(path.size-1)) {
				path.size.do{ var item = path.pop; this.selectorAt(*path).select(item) };
			};
			idle = false; noUpd=false; this.radiate;
			item
		}{ nil }	
	}
	select{|level, item|
		^this.selectAt(this.selected(level).drop(-1), item)
	}
	selectByDeltaAt{|path, delta|
		var res;
		noUpd=true;
		res = this.selectorAt(*path).selByDelta(delta);
		if (res.notNil) {
			if (path != this.selected(path.size-1)) {
				path.size.do{ var item = path.pop; this.selectorAt(*path).select(item) };
			};
			idle = false; noUpd=false; this.radiate 
		};
		^res
	}
	selectByDelta{|level, delta|
		var selected = this.selected(level).drop(-1);
		^if (selected.includes(nil)) {nil} { this.selectByDeltaAt(selected, delta) }
	}
	update {|who, what ...moreArgs| //this.logln("update:" + [who, what, moreArgs]);
		if (noUpd) { isChanged = true } { idle = false; this.changed //; this.logln("update")
	}}
	radiate{
		if (isChanged) { this.changed; isChanged=false  // ; this.logln("radiate");
	}}
}

Selector {
	var <wrapIndex, <whiteFunc, <src;	//src can be a SortedList!
	*new{|source, selIndex, whiteFunc| 
		^super.newCopyArgs(selIndex ? 0, whiteFunc).init(source) 
	}
	storeArgs { ^[src, wrapIndex, whiteFunc] }
	
	init{|source| // this.logln("init:" + [source, sel, whiteFunc]);
		if (source.isKindOf(SequenceableCollection).not) { src = source.asList 
		}{ src = source
		//src = source.deepCopy // deepCopy to copy function of SortedList put not all contents!!!
		}; 
//this.logln("src, wrapIndex, selected, whiteFunc" + [src, wrapIndex, this.selected, whiteFunc]);
	}
	select{|item| 
		^if (src.includes(item)) { 
//this.logln("item, selected:" + [item, this.selected, item == this.selected, item === this.selected]);
			if (item === this.selected) {item }{ //idendtity !!!
				wrapIndex=src.indexOf(item) ? 0;
				this.changed(\selection, item);
				item;
			}
		}{ nil }
	}
	selectIndex {|selIndex|
		var newSelection, oldSelection = this.selected;
		wrapIndex = selIndex;
		newSelection = this.selected;
		if ( newSelection !== oldSelection ) { this.changed(\selection, newSelection) }
	}
	selected { ^src.wrapAt(wrapIndex) }
}
/*
a = Selector([\a, \b, \c], 2)
b = a.asCompileString
c = b.interpret
c.selected

a = IdentitySelector([\a, \b, \c], 2)
a.remove(\c)
a.remove(\b)
a.selected
a.wrapIndex
b = a.asCompileString
c = b.interpret
c.add(\d)
c.selected
c.add(\e, true)
c.selected
*/
IdentitySelector : Selector { // src is a SequenceableCollection
	selByDelta{|delta|
		var newSelection, oldSelection = this.selected; 
		wrapIndex=wrapIndex+delta;
		newSelection = this.selected;
		^if ( newSelection === oldSelection ) { oldSelection } {
			this.changed(\selection, newSelection);  newSelection }
	}
	remove{|item|
		var currentSel = this.selected;
		var removed = src.remove(item);
		wrapIndex=src.indexOf(currentSel) ? 0;
		^removed
	}
	add{|item, isSelected=false|
		if (src.isEmpty) {isSelected=true};
		if (whiteFunc.notNil) { if (whiteFunc.value(item).not) { ^nil} };
		if (src.includes(item).not) { src = src.add(item) };
		^if (isSelected) { this.select(item) } {nil} //why this ?? -> crosscheck FourierScratching
	}
	addAll{|items, selectItem|
		if (whiteFunc.notNil) { items = items.select{|item| whiteFunc.value(item)} };
		if (src.isEmpty && items.includes(selectItem).not) {selectItem=items.first};
		items.do{|item| if (src.includes(item).not) { src = src.add(item) } }; 
			//src = src.addAll(items);
		^if (selectItem.notNil) { this.select(selectItem) } {nil}
	}
	moveSelectedBwd {
		var index = wrapIndex % src.size;
		if (src.notEmpty && (index != 0)) { 
			src = src[..(index-2)] ++ this.selected ++ src[index-1] ++ src[(index+1)..];
			wrapIndex = wrapIndex -1
		}
	}
	moveSelectedFwd { 
		var index = wrapIndex % src.size;
this.logln("moveSelectedFwd:" + index);
		if (src.notEmpty && ( index != (src.size-1))) {Ê
			src = src[..(index-1)] ++ src[index+1] ++ this.selected ++ src[(index+2)..];
			wrapIndex = wrapIndex +1
		}
	}
}

+ SortedList {
	storeArgs { ^[this.size.max(8), function.postln] } // does not work ??
	storeOn { arg stream;
		stream << "SortedList.new(" << this.size.max(8) << ", " << function.asCompileString << ")"
			<< ".addAll(" << array << ")";
	}
	
}
/*
IndexSelector : Selector { // src is an Integer 
	init{|sel| 
		selected = sel ? 0;
		wrapIndex = selected;
	}
	selByDelta{|delta| 
		if (delta !=0) { wrapIndex=wrapIndex+delta; ^selected = wrapIndex.mod(src) }
	}
	remove{|item| 
		if (item == selected) {this.selByDelta(1)};
		src = src -1;
		^item
	}
	add{|n, isSelected=false|
		src = src +1; 
		if (isSelected) {this.select(item)}
	}
}
*/