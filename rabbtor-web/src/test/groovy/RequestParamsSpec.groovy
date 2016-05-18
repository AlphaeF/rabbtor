import com.rabbtor.web.servlet.support.RequestParams
import spock.lang.Specification

/**
 * Created by Cagatay on 9.05.2016.
 */
class RequestParamsSpec extends Specification
{
    def 'test put'()
    {
        given:
        def params = new RequestParams();
        when:
        def map = [id: 20, names: ['foo', 'bar'], doubles: [11d, 22.0d]]
        params.put(map);
        then:
        assert params.getParameterValues('id').toList() == [20]
        assert params.getParameter('id') == 20
        assert params.getParameterValues('names').toList() == ['foo', 'bar']
    }

    def 'test set'()
    {
        given:
        def params = new RequestParams();
        when:
        params.set('id', 20);
        then:
        assert params.getParameter('id') == 20
        when:
        params.set('names', ['foo', 'bar'])
        then:
        assert params.getParameterValues('names').toList() == ['foo', 'bar']
        when:
        params.set('animals', ['cat', 'dog'] as String[])
        then:
        assert params.getParameterValues('animals').toList() == ['cat', 'dog']
    }

    def 'test append'()
    {
        given:
        def params = new RequestParams();
        when:
        params.append('names', 'foo')
        then:
        assert params.getParameterValues('names').toList() == ['foo']
        when:
        params.append('names', 'bar')
        then:
        assert params.getParameterValues('names').toList() == ['foo', 'bar']
        when:
        params.set('names', 'test')
        then:
        assert params.getParameterValues('names').toList() == ['test']
    }

    def 'test if can convert as request params with default conversion service'()
    {
        given:
        def params = new RequestParams();
        params.put(['id':20, names:['foo','bar'] as String[], date: new Date(), doubles: [1/3.0]])
        when:
        def requestParams = params.toRequestParameterMap(null)
        then:
        assert requestParams.size() == params.size();


    }
}
