function setupCrop(parms) {
	$('.mypic-crop').Jcrop({
		aspectRatio:1,
		setSelect:[0,0,512,512],
		boxWidth:512,
		boxHeight:512,
		onChange: function(c) {
			dojo.byId(parms.cropTop).value = c.y;
			dojo.byId(parms.cropLeft).value = c.x;
			dojo.byId(parms.cropWidth).value = c.w;
			dojo.byId(parms.cropHeight).value = c.h;
			dojo.style(dojo.byId(parms.smallPreview), {
				width: Math.round((25 / c.w) * $('.mypic-crop').width())+'px',
				height: Math.round((25 / c.h) * $('.mypic-crop').height())+'px',
				marginLeft: Math.round(-(25 / c.w) * c.x)+'px',
				marginTop: Math.round(-(25 / c.h) * c.y)+'px'
			}); 
			dojo.style(dojo.byId(parms.midPreview), {
				width: Math.round((50 / c.w) * $('.mypic-crop').width())+'px',
				height: Math.round((50 / c.h) * $('.mypic-crop').height())+'px',
				marginLeft: Math.round(-(50 / c.w) * c.x)+'px',
				marginTop: Math.round(-(50 / c.h) * c.y)+'px'
			}); 
			dojo.style(dojo.byId(parms.largePreview), {
				width: Math.round((75 / c.w) * $('.mypic-crop').width())+'px',
				height: Math.round((75 / c.h) * $('.mypic-crop').height())+'px',
				marginLeft: Math.round(-(75 / c.w) * c.x)+'px',
				marginTop: Math.round(-(75 / c.h) * c.y)+'px'
			}); 
			dojo.style(dojo.byId(parms.xLargePreview), {
				width: Math.round((128 / c.w) * $('.mypic-crop').width())+'px',
				height: Math.round((128 / c.h) * $('.mypic-crop').height())+'px',
				marginLeft: Math.round(-(128 / c.w) * c.x)+'px',
				marginTop: Math.round(-(128 / c.h) * c.y)+'px'
			}); 

		}
	});
}