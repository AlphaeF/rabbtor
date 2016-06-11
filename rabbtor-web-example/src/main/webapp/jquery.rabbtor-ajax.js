/*!
 * Rabbtor Unobtrusive Ajax v1.0.0-beta
 * Copyright 2016 Rabbytes, Inc.
 * Licensed under the MIT license
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


        self._registerListeners();
    };

    AjaxForm.VERSION = '1.0.0-beta';
    AjaxForm.DEFAULTS = {
        ajaxMode: 'replace'
    };

    $.extend(AjaxForm.prototype, {
        _getTargets: function () {
            if (this.options.ajaxTarget)
                return $(this.options.ajaxTarget);
            else
                return this.$el;
        },
        _placeResult: function (data) {
            var mode = this.options.ajaxMode || 'replace'
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
        },
        _registerListeners: function () {
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


}(jQuery)
