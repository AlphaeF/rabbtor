/*
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Rabbytes Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Rabbytes Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Rabbytes Incorporated.
 */
if (typeof jQuery === 'undefined') {
    throw new Error('Rabbtor Ajax JavaScript requires jQuery')
}

+function ($) {
    'use strict';

    var AjaxForm = function (element, options) {
        var self = this;
        var $el = this.$el = $(element);
        this.options = $.extend({}, AjaxForm.DEFAULTS, $el.data(), options);


        self._init();
    };

    AjaxForm.VERSION = '1.0.0-beta';
    AjaxForm.DEFAULTS = {
        ajaxMode: 'replace'
    };

    $.extend(AjaxForm.prototype, {

        /**
         * get target elements
         * @returns jQuery selector object for data-ajax-target selectors or the form itself
         * @private
         */
        _getTargets: function () {
            if (this.options.ajaxTarget)
                return $(this.options.ajaxTarget);
            else
                return this.$el;
        },

        /**
         * places the AJAX response result into target elements with the given data-ajax-mode setting.
         * default replacement mode is 'replace' which replaces all inner html with the AJAX result
         * @param AJAX response data
         * @private
         */
        _placeResult: function (data) {
            var self = this, mode = this.options.ajaxMode || 'replace';
            var $targets = this._getTargets();
            switch (mode) {
                case 'replace':
                case 'update':
                    $targets.html(data);
                    break;
                case 'prepend':
                case 'before':
                    $targets.prepend(data);
                    break;
                case 'append':
                case 'after':
                    $targets.append(data);
                    break;
            }

            self.processContent();
        },

        /**
         * Initialize
         * @private
         */
        _init: function () {
            var self = this, $el = self.$el, opts = self.options;
            var form = $el[0];

            self.submitHandler = function (e) {
                e.preventDefault();

                var ajaxOpts = {
                    url: opts.ajaxUrl || form.action,
                    type: opts.ajaxMethod || form.method || 'POST'
                };


                $.extend(ajaxOpts, {
                    success: function (data, status, xhr) {
                        var e = $.Event('success.rbt.ajaxForm');
                        $el.trigger(e, arguments);
                        if (e.isDefaultPrevented())
                            return false;

                        self._placeResult(data);
                        return true;
                    },
                    error: function (xhr, status, error) {
                        $el.trigger('error.rbt.ajaxForm', arguments);
                    },
                    complete: function () {
                        $el.trigger('complete.rbt.ajaxForm', arguments);
                    },
                    beforeSend: function () {
                        var e = $.Event('beforeSend.rbt.ajaxForm');
                        $el.trigger(e, arguments);
                        if (e.isDefaultPrevented())
                            return false;
                        return true;
                    }
                });

                var data = $el.serializeArray();

                // set click submit value of the submitting button
                if (self.submitValue)
                    data.push(self.submitValue);
                self.submitValue = null;


                data.push({name: "X-Requested-With", value: "XMLHttpRequest"});
                ajaxOpts.data = data;

                var e = $.Event('prepare.rbt.ajaxForm');

                $el.trigger(e, [ajaxOpts, data]);
                if (e.isDefaultPrevented()) {
                    return
                }

                $.ajax(ajaxOpts);

            };
            $el.on('submit', self.submitHandler);

            self.submitButtonHandler = function(e) {
                var $btn = $(this);
                var name = $btn.attr('name') || $btn.attr('data-ajax-submit')
                if (name && $btn[0].hasAttribute('value'))
                    self.submitValue = {name:name, value:$btn.attr('value')};
            };

            self.processContent();
        },

        /**
         * Processes the form child elements during initialization and after AJAX post
         * so that handlers for trigger buttons are registered or unregistered.
         */
        processContent: function() {
            var self = this, $el = self.$el;
            $("[type='submit'],[data-ajax-submit]", $el).off('click', self.submitButtonHandler);
            $("[type='submit'],[data-ajax-submit]", $el).on('click', self.submitButtonHandler);
        }



    });

    // AjaxForm PLUGIN DEFINITION
    // ========================

    function Plugin(option, value) {
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('rbt.ajaxForm');
            var options = typeof option == 'object' && option;

            if (!data)
                $this.data('rbt.ajaxForm', (data = new AjaxForm(this, options)));

            if (typeof option == 'string') {
                data[option](value);
            }
        });
    }

    var old = $.fn.ajaxForm;

    $.fn.ajaxForm = Plugin;
    $.fn.ajaxForm.Constructor = AjaxForm;

    // AJAXFORM NO CONFLICT
    // ====================

    $.fn.ajaxForm.noConflict = function () {
        $.fn.ajaxForm = old;
        return this
    };

    // AJAXFORM INIT
    $(function () {
        $("form[data-ajax='true']").ajaxForm();
    });


}(jQuery);
