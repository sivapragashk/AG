/*
 * SimpleModal OSX Style Modal Dialog
 * http://www.ericmmartin.com/projects/simplemodal/
 * http://code.google.com/p/simplemodal/
 *
 * Copyright (c) 2010 Eric Martin - http://ericmmartin.com
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Revision: $Id: osx.js 238 2010-03-11 05:56:57Z emartin24 $
 */

jQuery(function ($) {
	var OSX = {
		container: null,
		init: function () {
			$("#pipsignup").click(function (e) {
				e.preventDefault();	
				var _top = (screen.height/2)-(500/2);
				var _left = (screen.width/2)-(300/2);
				$("#modal-content").modal({
					overlayId: 'overlay',
					containerId: 'modal-container',
					closeHTML: null,
					minHeight: 0,
					opacity: 65, 
					position: [_top],
					overlayClose: true,
					onOpen: OSX.open,
					onClose: OSX.close
				});
			});
		},
		open: function (d) {
			var self = this;
			self.container = d.container[0];
			d.overlay.fadeIn('fast', function () {
				d.container.fadeIn('fast', function () {
					setTimeout(function () {
						var h = $("#modal-data", self.container).height()
							+ title.height()
							+ 20; // padding
						var _left = (screen.width/2)-(600/2);
						var _top = (screen.height/2)-(500/2);
						d.container.animate(
							{height: h, top:_top}, 
							200,
							function () {
								$("div.close", self.container).show();
								$("#modal-data", self.container).show();
							}
						);
					}, 300);
					$("#modal-content", self.container).show();
					var title = $("#modal-title", self.container);
					title.show();
				});
			})
		},
		close: function (d) {
			var self = this; // this = SimpleModal object
			d.container.animate(
				
				500,
				function () {
					$.modal.close();
				}
			);
		}
	};

	OSX.init();

});